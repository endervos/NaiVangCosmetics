package demoweb.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "voucher_customer_item")
public class VoucherCustomerItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voucher_customer_item_id")
    private Integer voucherCustomerItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public VoucherCustomerItem() {}

    public Integer getVoucherCustomerItemId() {
        return voucherCustomerItemId;
    }

    public void setVoucherCustomerItemId(Integer voucherCustomerItemId) {
        this.voucherCustomerItemId = voucherCustomerItemId;
    }

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}