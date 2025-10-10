package demoweb.demo.service;

import demoweb.demo.entity.Cart;
import demoweb.demo.entity.CartItem;
import demoweb.demo.entity.Item;
import demoweb.demo.repository.CartItemRepository;
import demoweb.demo.repository.CartRepository;
import demoweb.demo.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ItemRepository itemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.itemRepository = itemRepository;
    }

    public Optional<Cart> findCartByAccountId(String accountId) {
        return cartRepository.findByAccountId(accountId);
    }

    public Optional<Item> findItemById(Integer itemId) {
        return itemRepository.findById(itemId);
    }

    public List<CartItem> getCartItems(Integer cartId) {
        return cartItemRepository.findById_CartId(cartId);
    }
}