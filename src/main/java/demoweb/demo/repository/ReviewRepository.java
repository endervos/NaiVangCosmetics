package demoweb.demo.repository;

import demoweb.demo.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer>, JpaSpecificationExecutor<Review> {

    List<Review> findByItem_ItemId(Integer itemId);

    List<Review> findByCustomer_CustomerId(Integer customerId);
}