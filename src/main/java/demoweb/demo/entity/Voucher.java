package demoweb.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "voucher")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer voucherId;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(length = 255)
    private String description;

    @Column(name = "discount_percent", nullable = false)
    private Integer discountPercent;

    @Column(name = "max_uses")
    private Integer maxUses;

    @Column(name = "used_count")
    private Integer usedCount = 0;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    public Integer getVoucherId() { return voucherId; }
    public String getCode() { return code; }
    public Integer getDiscountPercent() { return discountPercent; }
    public Integer getMaxUses() { return maxUses; }
    public Integer getUsedCount() { return usedCount; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public Boolean getIsActive() { return isActive; }

    public void setVoucherId(Integer voucherId) { this.voucherId = voucherId; }
    public void setCode(String code) { this.code = code; }
    public void setDiscountPercent(Integer discountPercent) { this.discountPercent = discountPercent; }
    public void setMaxUses(Integer maxUses) { this.maxUses = maxUses; }
    public void setUsedCount(Integer usedCount) { this.usedCount = usedCount; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public void setIsActive(Boolean active) { isActive = active; }
}