package com.assignment.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PromoCodeDTO {

    @NotNull(message = "Promo code is required!")
    @Size(min = 1, max = 100, message = "Promo code must be between 1 and 100 characters")
    private String code;

    @NotNull(message = "DiscountPercentage is required!")
    @DecimalMin(value = "0.01", message = "Discount percentage must be greater than 0")
    private Double discountPercentage;

    @NotNull(message = "Minimum order amount is required!")
    @DecimalMin(value = "0.01", message = "Minimum order amount must be greater than 0")
    private Double minOrderAmount;

    @NotNull(message = "Product specificity must be specified")
    private Boolean isProductSpecific;

    private Integer productId;

    @NotNull(message = "Active status must be specified")
    private Boolean isActive;

    @NotNull(message = "Expiry date must be specified in format YYYY-MM-DD")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Expiry date must be in the format YYYY-MM-DD")
    private String expiryDate;

    private String productName;

    // Getters and setters

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Double getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(Double minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public Boolean getIsProductSpecific() {
        return isProductSpecific;
    }

    public void setIsProductSpecific(Boolean isProductSpecific) {
        this.isProductSpecific = isProductSpecific;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}
