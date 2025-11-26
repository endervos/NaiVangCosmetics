package demoweb.demo.entity;

import java.io.Serializable;
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
        return cartId.equals(that.cartId) && itemId.equals(that.itemId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(cartId, itemId);
    }
}
