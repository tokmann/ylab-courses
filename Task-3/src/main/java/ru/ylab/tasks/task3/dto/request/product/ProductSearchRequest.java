package ru.ylab.tasks.task3.dto.request.product;

import java.math.BigDecimal;

public class ProductSearchRequest {

    private String keyword;
    private String category;
    private String brand;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    public ProductSearchRequest() {}

    public ProductSearchRequest(String keyword, String category, String brand, BigDecimal minPrice, BigDecimal maxPrice) {
        this.keyword = keyword;
        this.category = category;
        this.brand = brand;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getCategory() {
        return category;
    }

    public String getBrand() {
        return brand;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

}
