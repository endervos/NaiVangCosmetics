package demoweb.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(exclude = {"item", "customer"})
@Table(name = "review")
public class Review implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID của đánh giá */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer reviewId;

    /** Sản phẩm được đánh giá */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "item_id")
    @JsonBackReference(value = "item-reviews") // Ngăn vòng lặp JSON giữa Item ↔ Review
    private Item item;

    /** Khách hàng thực hiện đánh giá */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    @JsonBackReference(value = "customer-reviews") // Ngăn vòng lặp JSON giữa Customer ↔ Review
    private Customer customer;

    /** Số sao đánh giá (1–5) */
    @Column(name = "rating", nullable = false)
    private Integer rating;

    /** Nội dung nhận xét */
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    /** Ngày tạo review */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    /** Tự động set thời gian khi tạo review */
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
