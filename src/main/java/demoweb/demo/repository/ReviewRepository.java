package demoweb.demo.repository;

import demoweb.demo.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer>, JpaSpecificationExecutor<Review> {

    // Lấy tất cả đánh giá của một sản phẩm cụ thể
    List<Review> findByItem_ItemId(Integer itemId);

    // Lấy tất cả đánh giá của một khách hàng cụ thể
    List<Review> findByCustomer_CustomerId(Integer customerId);

    // Lấy tất cả đánh giá theo điểm rating
    List<Review> findByRating(Integer rating);

    // Lấy các review của 1 sản phẩm, sắp xếp mới nhất trước
    List<Review> findByItem_ItemIdOrderByCreatedAtDesc(Integer itemId);
}
