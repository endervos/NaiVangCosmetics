package demoweb.demo.repository;

import demoweb.demo.entity.VoucherCustomerItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoucherCustomerItemRepository extends JpaRepository<VoucherCustomerItem, Integer> {

    @Query(value = """
        SELECT 
            i.item_id,
            i.name,
            i.price,
            i.description,
            i.color,
            MAX(v.discount_percent) as max_discount_percent,
            (i.price * MAX(v.discount_percent) / 100) as discount_amount,
            (i.price - (i.price * MAX(v.discount_percent) / 100)) as final_price
        FROM voucher_customer_item vci
        JOIN item i ON vci.item_id = i.item_id
        JOIN voucher v ON vci.voucher_id = v.voucher_id
        WHERE v.is_active = true
          AND v.start_date <= NOW()
          AND v.end_date >= NOW()
        GROUP BY i.item_id, i.name, i.price, i.description, i.color
        ORDER BY discount_amount DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> findTop5FlashSaleItems();
}