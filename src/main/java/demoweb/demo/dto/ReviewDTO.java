package demoweb.demo.dto;

import demoweb.demo.entity.Review;

import java.util.Date;

public class ReviewDTO {
    private Integer reviewId;
    private Integer itemId;
    private Integer customerId;
    private String customerName;
    private Integer rating;
    private String comment;
    private Date createdAt;

    public ReviewDTO(Review review) {
        this.reviewId = review.getReviewId();
        this.itemId = review.getItem().getItemId();
        if (review.getCustomer() != null) {
            this.customerId = review.getCustomer().getCustomerId();
            this.customerName = review.getCustomer().getUser().getFullname();
        } else {
            this.customerId = null;
            this.customerName = "Ẩn danh";
        }
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.createdAt = review.getCreatedAt();
    }

    // getters và setters
    public Integer getReviewId() { return reviewId; }
    public void setReviewId(Integer reviewId) { this.reviewId = reviewId; }
    public Integer getItemId() { return itemId; }
    public void setItemId(Integer itemId) { this.itemId = itemId; }
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
