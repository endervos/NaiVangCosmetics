package demoweb.demo.controller;

import demoweb.demo.dto.ReviewDTO;
import demoweb.demo.entity.Customer;
import demoweb.demo.entity.Item;
import demoweb.demo.entity.Review;
import demoweb.demo.entity.User;
import demoweb.demo.repository.CustomerRepository;
import demoweb.demo.service.CustomerService;
import demoweb.demo.service.ItemService;
import demoweb.demo.service.ReviewService;
import demoweb.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.List;
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

    @Autowired
    public ReviewController(ReviewService reviewService,
                            ItemService itemService,
                            UserService userService,
                            CustomerService customerService,
                            CustomerRepository customerRepository) {
        this.reviewService = reviewService;
        this.itemService = itemService;
        this.userService = userService;
        this.customerService = customerService;
        this.customerRepository = customerRepository;
    }

    @PostMapping
    public ResponseEntity<?> createReview(@Valid @RequestBody Review review, Principal principal) {
        try {
            if (principal != null) {
                User user = userService.getUserByEmail(principal.getName())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));
                Customer customer = customerRepository.findByUser(user)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy Customer"));
                review.setCustomer(customer);
            }

            Review savedReview = reviewService.save(review);
            return ResponseEntity.ok(new ReviewDTO(savedReview));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Không thể tạo review: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@PathVariable("id") Integer id,
                                          @Valid @RequestBody Review updatedReview) {
        try {
            Review review = reviewService.update(id, updatedReview);
            return ResponseEntity.ok(new ReviewDTO(review));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi cập nhật review: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable("id") Integer id) {
        try {
            reviewService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Không thể xóa review: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable("id") Integer id) {
        Review review = reviewService.getById(id);
        if (review != null) {
            return ResponseEntity.ok(new ReviewDTO(review));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        List<ReviewDTO> dtos = reviewService.getAll()
                .stream()
                .map(ReviewDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<?> getReviewsByItem(@PathVariable("itemId") Integer itemId) {
        List<Review> reviews = reviewService.getByItemId(itemId);
        if (reviews.isEmpty()) {
            return ResponseEntity.ok("Sản phẩm này chưa có đánh giá nào.");
        }
        List<ReviewDTO> dtos = reviews.stream()
                .map(ReviewDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getReviewsByCustomer(@PathVariable("customerId") Integer customerId) {
        List<Review> reviews = reviewService.getByCustomerId(customerId);
        if (reviews.isEmpty()) {
            return ResponseEntity.ok("Khách hàng này chưa viết đánh giá nào.");
        }
        List<ReviewDTO> dtos = reviews.stream()
                .map(ReviewDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
