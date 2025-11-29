package demoweb.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Integer addressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "district", length = 100)
    private String district;

    @Column(name = "street", length = 200)
    private String street;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "id_address_default")
    private Boolean idAddressDefault;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Address() {
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.idAddressDefault == null) {
            this.idAddressDefault = false;
        }
    }

    // Getters and Setters
    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getIdAddressDefault() {
        return idAddressDefault;
    }

    public void setIdAddressDefault(Boolean idAddressDefault) {
        this.idAddressDefault = idAddressDefault;
    }

    public Boolean getIsDefault() {
        return this.idAddressDefault;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Address{" +
                "addressId=" + addressId +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", street='" + street + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", idAddressDefault=" + idAddressDefault +
                ", createdAt=" + createdAt +
                '}';
    }
}