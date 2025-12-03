package com.nebulak.model;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


// Enum for Coupon Type (e.g., PERCENTAGE, FIXED_AMOUNT, FREE_SHIPPING)
enum DiscountType {
    PERCENTAGE,
    FIXED_AMOUNT,
    FREE_SHIPPING
}

@Entity
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code; // The actual code users enter (e.g., "SAVE20")

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    @Column(nullable = false)
    private double discountValue; // e.g., 20 for PERCENTAGE, 10.00 for FIXED_AMOUNT

    @Column(name = "minimum_purchase_amount")
    private double minPurchaseAmount; // Minimum cart value required

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "usage_limit_total")
    private Integer usageLimitTotal; // Total number of times this coupon can be used overall

    @Column(name = "usage_count")
    private int usageCount = 0; // Current count of how many times it has been used

    @Column(name = "usage_limit_per_user")
    private Integer usageLimitPerUser; // Limit per single customer

    @Column(nullable = false)
    private boolean isActive = true;

    // Optional: Link coupons to specific products or categories
    // @ManyToMany
    // @JoinTable(
    //     name = "coupon_product",
    //     joinColumns = @JoinColumn(name = "coupon_id"),
    //     inverseJoinColumns = @JoinColumn(name = "product_id")
    // )
    // private Set<Product> applicableProducts = new HashSet<>();

    // Optional: Track which orders used this coupon
    // @OneToMany(mappedBy = "appliedCoupon")
    // @JsonIgnore
    // private Set<Order> orders = new HashSet<>();


    // Constructors, Getters, and Setters...
    public Coupon() {}

    public Coupon(Long id, String code, String description, DiscountType discountType, double discountValue,
                  double minPurchaseAmount, LocalDateTime startDate, LocalDateTime endDate, Integer usageLimitTotal,
                  int usageCount, Integer usageLimitPerUser, boolean isActive) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minPurchaseAmount = minPurchaseAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.usageLimitTotal = usageLimitTotal;
        this.usageCount = usageCount;
        this.usageLimitPerUser = usageLimitPerUser;
        this.isActive = isActive;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }

    public double getMinPurchaseAmount() {
        return minPurchaseAmount;
    }

    public void setMinPurchaseAmount(double minPurchaseAmount) {
        this.minPurchaseAmount = minPurchaseAmount;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getUsageLimitTotal() {
        return usageLimitTotal;
    }

    public void setUsageLimitTotal(Integer usageLimitTotal) {
        this.usageLimitTotal = usageLimitTotal;
    }

    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }

    public Integer getUsageLimitPerUser() {
        return usageLimitPerUser;
    }

    public void setUsageLimitPerUser(Integer usageLimitPerUser) {
        this.usageLimitPerUser = usageLimitPerUser;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}




