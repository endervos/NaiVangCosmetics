package demoweb.demo.service;

import demoweb.demo.entity.Customer;
import demoweb.demo.entity.Item;
import demoweb.demo.entity.Review;
import demoweb.demo.entity.User;
import demoweb.demo.repository.ReviewRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Transactional(readOnly = true)
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Review> getReviewById(Integer reviewId) {
        return reviewRepository.findById(reviewId);
    }

    @Transactional(readOnly = true)
    public List<Review> getReviewsByItemId(Integer itemId) {
        return reviewRepository.findByItem_ItemId(itemId);
    }

    @Transactional(readOnly = true)
    public List<Review> getReviewsByItemIdWithUser(Integer itemId) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("GetReviewsByItemIdWithUser");
        query.registerStoredProcedureParameter(1, Integer.class, jakarta.persistence.ParameterMode.IN);
        query.setParameter(1, itemId);
        @SuppressWarnings("unchecked")
        List<Object[]> rawResults = query.getResultList();
        List<Review> reviews = new ArrayList<>();
        for (Object[] row : rawResults) {
            Review review = new Review();
            review.setReviewId((Integer) row[0]);
            review.setRating((Integer) row[1]);
            review.setComment((String) row[2]);
            if (row[3] != null) {
                java.sql.Timestamp createdTimestamp = (java.sql.Timestamp) row[3];
                review.setCreatedAt(new Date(createdTimestamp.getTime()));
            }
            if (row[4] != null) {
                Item item = new Item();
                item.setItemId((Integer) row[4]);
                review.setItem(item);
            }
            if (row[5] != null) {
                Customer customer = new Customer();
                customer.setCustomerId((Integer) row[5]);
                review.setCustomer(customer);
                if (row[6] != null) {
                    User user = new User();
                    user.setUserId(String.valueOf(row[6]));
                    customer.setUser(user);
                }
            }
            reviews.add(review);
        }
        return reviews;
    }

    @Transactional(readOnly = true)
    public List<Review> getAllReviewsWithDetails() {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("GetAllReviewsWithDetails");
        @SuppressWarnings("unchecked")
        List<Object[]> rawResults = query.getResultList();
        List<Review> reviews = new ArrayList<>();
        for (Object[] row : rawResults) {
            Review review = new Review();
            review.setReviewId((Integer) row[0]);
            review.setRating((Integer) row[1]);
            review.setComment((String) row[2]);
            if (row[3] != null) {
                java.sql.Timestamp createdTimestamp = (java.sql.Timestamp) row[3];
                review.setCreatedAt(new Date(createdTimestamp.getTime()));
            }
            if (row[4] != null) {
                Item item = new Item();
                item.setItemId((Integer) row[4]);
                item.setName((String) row[5]);
                review.setItem(item);
            }
            if (row[6] != null) {
                Customer customer = new Customer();
                customer.setCustomerId((Integer) row[6]);
                review.setCustomer(customer);
                if (row[7] != null) {
                    User user = new User();
                    user.setUserId(String.valueOf(row[7]));
                    customer.setUser(user);
                }
            }
            reviews.add(review);
        }
        return reviews;
    }

    @Transactional(readOnly = true)
    public List<Review> getReviewsByCustomerId(Integer customerId) {
        return reviewRepository.findByCustomer_CustomerId(customerId);
    }

    @Transactional
    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    @Transactional
    public void delete(Integer reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    @Transactional(readOnly = true)
    public Double getAverageRating(Integer itemId) {
        List<Review> reviews = reviewRepository.findByItem_ItemId(itemId);
        if (reviews.isEmpty()) return 0.0;
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    @Transactional(readOnly = true)
    public int getReviewCount(Integer itemId) {
        return reviewRepository.findByItem_ItemId(itemId).size();
    }

    public String getRatingStars(Double rating) {
        if (rating == null) return "☆☆☆☆☆";
        int full = (int) Math.floor(rating);
        int half = (rating - full >= 0.5) ? 1 : 0;
        int empty = 5 - full - half;
        return "★".repeat(full) + (half == 1 ? "⯨" : "") + "☆".repeat(empty);
    }
}