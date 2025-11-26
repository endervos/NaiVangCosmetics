package demoweb.demo.repository;

import demoweb.demo.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer>, JpaSpecificationExecutor<Review> {

    /**
     * Lấy tất cả đánh giá của một sản phẩm cụ thể theo itemId
     */
    List<Review> findByItem_ItemId(Integer itemId);

    /**
     * Lấy tất cả đánh giá của một khách hàng cụ thể theo customerId
     */
    List<Review> findByCustomer_CustomerId(Integer customerId);

    /**
     * Lấy danh sách review theo số điểm rating cụ thể
     */
    List<Review> findByRating(Integer rating);

    /**
     * Lấy tất cả review của 1 sản phẩm, sắp xếp theo thời gian tạo (mới nhất trước)
     */
    List<Review> findByItem_ItemIdOrderByCreatedAtDesc(Integer itemId);

    /**
     * Lấy các review của sản phẩm kèm thông tin user (dùng JOIN FETCH để tránh lazy loading)
     */
    @Query("""
        SELECT r FROM Review r
        LEFT JOIN FETCH r.customer c
        LEFT JOIN FETCH c.user u
        WHERE r.item.itemId = :itemId
        ORDER BY r.createdAt DESC
        """)
    List<Review> findByItemIdWithUser(@Param("itemId") Integer itemId);

}
