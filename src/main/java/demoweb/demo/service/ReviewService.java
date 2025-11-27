package demoweb.demo.service;

import demoweb.demo.entity.Review;
import demoweb.demo.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    public Review save(Review review) {
        review.setCreatedAt(new Date());
        return reviewRepository.save(review);
    }

    @Transactional
    public void delete(Integer id) {
        if (!reviewRepository.existsById(id)) {
            throw new NoSuchElementException("Không tìm thấy review có ID: " + id);
        }
        reviewRepository.deleteById(id);
    }

    @Transactional
    public Review update(Integer id, Review updatedReview) {
        Review existing = requireOne(id);
        existing.setRating(updatedReview.getRating());
        existing.setComment(updatedReview.getComment());
        return reviewRepository.save(existing);
    }

    public Review getById(Integer id) {
        return requireOne(id);
    }

    public List<Review> getAll() {
        return reviewRepository.findAllWithDetails();
    }

    public List<Review> getByItemId(Integer itemId) {
        return reviewRepository.findByItemIdWithUser(itemId);
    }

    public List<Review> getByCustomerId(Integer customerId) {
        return reviewRepository.findByCustomer_CustomerId(customerId);
    }

    private Review requireOne(Integer id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy review có ID: " + id));
    }

    public Double getAverageRating(Integer itemId) {
        List<Review> reviews = reviewRepository.findByItem_ItemId(itemId);
        if (reviews == null || reviews.isEmpty()) return 0.0;

        double avg = reviews.stream()
                .filter(r -> r.getRating() != null)
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        return Math.round(avg * 10.0) / 10.0;
    }

    public int getReviewCount(Integer itemId) {
        return reviewRepository.findByItem_ItemId(itemId).size();
    }

    public String getRatingStars(Double averageRating) {
        if (averageRating == null) averageRating = 0.0;
        int full = (int) Math.floor(averageRating);
        boolean half = (averageRating - full) >= 0.5;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < full; i++) {
            sb.append("<i class='fa fa-star'></i>");
        }
        if (half) {
            sb.append("<i class='fa fa-star-half-o'></i>");
        }
        for (int i = full + (half ? 1 : 0); i < 5; i++) {
            sb.append("<i class='fa fa-star-o'></i>");
        }
        return sb.toString();
    }
}