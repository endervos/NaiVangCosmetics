package demoweb.demo.service;

import demoweb.demo.entity.Cart;
import demoweb.demo.entity.CartItem;
import demoweb.demo.repository.CartItemRepository;
import demoweb.demo.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public Optional<Cart> findCartByAccountId(String accountId) {
        return cartRepository.findByAccountId(accountId);
    }

    public List<CartItem> getCartItems(Integer cartId) {
        return cartItemRepository.findById_CartId(cartId);
    }
}