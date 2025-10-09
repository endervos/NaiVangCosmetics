package demoweb.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_item")
public class CartItem {

    @EmbeddedId
    private CartItemId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cartId") // Liên kết với khóa chính Cart
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("itemId")
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    public CartItem() {}

    // Constructor bổ sung để khởi tạo nhanh
    public CartItem(CartItemId id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    // Constructor tiện dụng khi có Cart và Item
    public CartItem(Cart cart, Item item, Integer quantity) {
        this.cart = cart;
        this.item = item;
        this.quantity = quantity;
        this.id = new CartItemId(cart.getCartId(), item.getItemId());
    }

    // ===== Getter & Setter =====
    public CartItemId getId() {
        return id;
    }

    public void setId(CartItemId id) {
        this.id = id;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}