package demoweb.demo.repository;

import demoweb.demo.entity.Order;
import demoweb.demo.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByCustomer_CustomerId(Integer customerId);

    List<Order> findByCustomer_CustomerIdAndStatus(Integer customerId, OrderStatus status);

    List<Order> findByPlacedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}