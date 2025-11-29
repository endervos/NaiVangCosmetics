package demoweb.demo.repository;

import demoweb.demo.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findByOrder_OrderId(Integer orderId);

    @Query(value = """
        SELECT oi.item_id, i.name, i.price, i.description, i.color,
               SUM(oi.quantity) as total_sold
        FROM order_item oi
        JOIN item i ON oi.item_id = i.item_id
        WHERE i.category_id = :categoryId
        GROUP BY oi.item_id, i.name, i.price, i.description, i.color
        ORDER BY total_sold DESC
        LIMIT 10
        """, nativeQuery = true)
    List<Object[]> findTop10BestsellersByCategory(@Param("categoryId") Integer categoryId);
}
