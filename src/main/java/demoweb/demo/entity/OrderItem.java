package demoweb.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderItemId;

    @ManyToOne @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne @JoinColumn(name = "item_id")
    private Item item;

    private int quantity;
    private int preDiscountPrice;
    private int totalPriceCents;

    public OrderItem() {
    }

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPreDiscountPrice() {
        return preDiscountPrice;
    }

    public void setPreDiscountPrice(int preDiscountPrice) {
        this.preDiscountPrice = preDiscountPrice;
    }

    public int getTotalPriceCents() {
        return totalPriceCents;
    }

    public void setTotalPriceCents(int totalPriceCents) {
        this.totalPriceCents = totalPriceCents;
    }
}