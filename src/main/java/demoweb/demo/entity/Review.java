package demoweb.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
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
}
