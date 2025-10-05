package demoweb.demo.controller;

import demoweb.demo.entity.Review;
import demoweb.demo.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Tạo review mới
    @PostMapping
    public Review createReview(@Valid @RequestBody Review review) {
        return reviewService.save(review);
    }

    // Xóa review theo ID
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable("id") Integer id) {
        reviewService.delete(id);
    }

    // Cập nhật review (rating, comment)
    @PutMapping("/{id}")
    public Review updateReview(@PathVariable("id") Integer id,
                               @Valid @RequestBody Review updatedReview) {
        return reviewService.update(id, updatedReview);
    }

    // Lấy review theo ID
    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable("id") Integer id) {
        return reviewService.getById(id);
    }

    // Lấy toàn bộ review
    @GetMapping
    public List<Review> getAllReviews() {
        return reviewService.getAll();
    }

    // Lấy review theo itemId
    @GetMapping("/item/{itemId}")
    public List<Review> getReviewsByItem(@PathVariable("itemId") Integer itemId) {
        return reviewService.getByItemId(itemId);
    }

    // Lấy review theo customerId
    @GetMapping("/customer/{customerId}")
    public List<Review> getReviewsByCustomer(@PathVariable("customerId") Integer customerId) {
        return reviewService.getByCustomerId(customerId);
    }
}
