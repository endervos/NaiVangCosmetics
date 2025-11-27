package demoweb.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "order_item")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    @JsonIgnoreProperties({"category", "inventory", "hibernateLazyInitializer", "handler"})
    private Item item;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "pre_discount_price", nullable = false)
    private Integer preDiscountPrice;

    @Column(name = "total_price_cents", nullable = false)
    private Integer totalPriceCents;

    // ===== Getter & Setter =====
    public Integer getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Integer orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
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

    public Integer getPreDiscountPrice() {
        return preDiscountPrice;
    }

    public void setPreDiscountPrice(Integer preDiscountPrice) {
        this.preDiscountPrice = preDiscountPrice;
    }

    public Integer getTotalPriceCents() {
        return totalPriceCents;
    }

    public void setTotalPriceCents(Integer totalPriceCents) {
        this.totalPriceCents = totalPriceCents;
    }
}
