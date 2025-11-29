package demoweb.demo.entity;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class CartItemId implements Serializable {

    @Column(name = "cart_id")
    private Integer cartId;

    @Column(name = "item_id")
    private Integer itemId;

    public CartItemId() {
    }

    public CartItemId(Integer cartId, Integer itemId) {
        this.cartId = cartId;
        this.itemId = itemId;
    }

    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartItemId)) return false;
        CartItemId that = (CartItemId) o;
        return Objects.equals(cartId, that.cartId) && Objects.equals(itemId, that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartId, itemId);
    }
}