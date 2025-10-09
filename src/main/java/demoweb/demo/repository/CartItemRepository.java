package demoweb.demo.repository;

import demoweb.demo.entity.CartItem;
import demoweb.demo.entity.CartItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, CartItemId> {
    List<CartItem> findById_CartId(Integer cartId);
}