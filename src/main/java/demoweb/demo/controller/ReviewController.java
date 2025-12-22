package demoweb.demo.controller;

import demoweb.demo.dto.ReviewDTO;
import demoweb.demo.entity.Customer;
import demoweb.demo.entity.Item;
import demoweb.demo.entity.Review;
import demoweb.demo.entity.User;
import demoweb.demo.repository.CustomerRepository;
import demoweb.demo.repository.ItemRepository;
import demoweb.demo.security.EncryptionUtil;
import demoweb.demo.service.CustomerService;
import demoweb.demo.service.ItemService;
import demoweb.demo.service.ReviewService;
import demoweb.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewService reviewService;
    private final ItemService itemService;
    private final UserService userService;
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;
    private final ItemRepository itemRepository;
    private final EncryptionUtil encryptionUtil;

    @Autowired
    public ReviewController(ReviewService reviewService,
                            ItemService itemService,
                            UserService userService,
                            CustomerService customerService,
                            CustomerRepository customerRepository,
                            ItemRepository itemRepository,
                            EncryptionUtil encryptionUtil) {
        this.reviewService = reviewService;
        this.itemService = itemService;
        this.userService = userService;
        this.customerService = customerService;
        this.customerRepository = customerRepository;
        this.itemRepository = itemRepository;
        this.encryptionUtil = encryptionUtil;
    }

    @PostMapping
    public ResponseEntity<?> createReview(@Valid @RequestBody Map<String, Object> reviewData,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).body("Vui lòng đăng nhập để đánh giá");
            }
            User user = userService.getUserByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));
            Customer customer = customerRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Customer"));
            String encryptedItemId = (String) reviewData.get("encryptedItemId");
            if (encryptedItemId == null || encryptedItemId.isEmpty()) {
                return ResponseEntity.badRequest().body("Thiếu thông tin sản phẩm");
            }
            String decryptedItemId = encryptionUtil.decrypt(encryptedItemId);
            Integer itemId = Integer.parseInt(decryptedItemId);
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
            Review review = new Review();
            review.setCustomer(customer);
            review.setItem(item);
            review.setRating((Integer) reviewData.get("rating"));
            review.setComment((String) reviewData.get("comment"));
            Review savedReview = reviewService.save(review);
            ReviewDTO dto = new ReviewDTO(savedReview);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("review", dto);
            response.put("encryptedReviewId", encryptionUtil.encrypt(savedReview.getReviewId().toString()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Không thể tạo review: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{encryptedId}")
    public ResponseEntity<?> updateReview(@PathVariable("encryptedId") String encryptedId,
                                          @Valid @RequestBody Map<String, Object> reviewData,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).body("Vui lòng đăng nhập");
            }
            String decryptedId = encryptionUtil.decrypt(encryptedId);
            Integer reviewId = Integer.parseInt(decryptedId);
            Review existingReview = reviewService.getReviewById(reviewId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy review"));
            User user = userService.getUserByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));
            Customer customer = customerRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Customer"));
            if (!existingReview.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
                return ResponseEntity.status(403).body("Bạn không có quyền sửa review này");
            }
            existingReview.setRating((Integer) reviewData.get("rating"));
            existingReview.setComment((String) reviewData.get("comment"));
            Review savedReview = reviewService.save(existingReview);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("review", new ReviewDTO(savedReview));
            response.put("encryptedReviewId", encryptionUtil.encrypt(savedReview.getReviewId().toString()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi cập nhật review: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{encryptedId}")
    public ResponseEntity<?> deleteReview(@PathVariable("encryptedId") String encryptedId,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).body("Vui lòng đăng nhập");
            }
            String decryptedId = encryptionUtil.decrypt(encryptedId);
            Integer reviewId = Integer.parseInt(decryptedId);
            Review existingReview = reviewService.getReviewById(reviewId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy review"));
            User user = userService.getUserByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));
            Customer customer = customerRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Customer"));
            if (!existingReview.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
                return ResponseEntity.status(403).body("Bạn không có quyền xóa review này");
            }
            reviewService.delete(reviewId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã xóa review thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Không thể xóa review: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyReviews(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).body("Vui lòng đăng nhập");
            }
            User user = userService.getUserByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));
            Customer customer = customerRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Customer"));
            List<Review> reviews = reviewService.getReviewsByCustomerId(customer.getCustomerId());
            if (reviews.isEmpty()) {
                return ResponseEntity.ok("Bạn chưa viết đánh giá nào.");
            }
            List<Map<String, Object>> reviewsWithEncryption = reviews.stream()
                    .map(review -> {
                        Map<String, Object> reviewMap = new HashMap<>();
                        reviewMap.put("reviewId", review.getReviewId());
                        reviewMap.put("encryptedReviewId", encryptionUtil.encrypt(review.getReviewId().toString()));
                        reviewMap.put("rating", review.getRating());
                        reviewMap.put("comment", review.getComment());
                        reviewMap.put("createdAt", review.getCreatedAt());
                        reviewMap.put("itemName", review.getItem().getName());
                        reviewMap.put("encryptedItemId", encryptionUtil.encrypt(review.getItem().getItemId().toString()));
                        return reviewMap;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(reviewsWithEncryption);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/item/{encryptedItemId}")
    public ResponseEntity<?> getReviewsByItem(@PathVariable("encryptedItemId") String encryptedItemId) {
        try {
            String decryptedItemId = encryptionUtil.decrypt(encryptedItemId);
            Integer itemId = Integer.parseInt(decryptedItemId);
            List<Review> reviews = reviewService.getReviewsByItemId(itemId);
            if (reviews.isEmpty()) {
                return ResponseEntity.ok("Sản phẩm này chưa có đánh giá nào.");
            }
            List<Map<String, Object>> reviewsWithEncryption = reviews.stream()
                    .map(review -> {
                        Map<String, Object> reviewMap = new HashMap<>();
                        reviewMap.put("reviewId", review.getReviewId());
                        reviewMap.put("encryptedReviewId", encryptionUtil.encrypt(review.getReviewId().toString()));
                        reviewMap.put("rating", review.getRating());
                        reviewMap.put("comment", review.getComment());
                        reviewMap.put("createdAt", review.getCreatedAt());
                        reviewMap.put("customerName", review.getCustomer().getUser().getFullname());
                        return reviewMap;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(reviewsWithEncryption);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
}