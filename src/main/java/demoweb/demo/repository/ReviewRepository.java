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

    List<Review> findByItem_ItemId(Integer itemId);

    List<Review> findByCustomer_CustomerId(Integer customerId);

    @Query("""
        SELECT r FROM Review r
        LEFT JOIN FETCH r.customer c
        LEFT JOIN FETCH c.user u
        WHERE r.item.itemId = :itemId
        ORDER BY r.createdAt DESC
        """)
    List<Review> findByItemIdWithUser(@Param("itemId") Integer itemId);

    @Query("""
        SELECT DISTINCT r FROM Review r
        LEFT JOIN FETCH r.item i
        LEFT JOIN FETCH r.customer c
        LEFT JOIN FETCH c.user u
        ORDER BY r.createdAt DESC
        """)
    List<Review> findAllWithDetails();
}