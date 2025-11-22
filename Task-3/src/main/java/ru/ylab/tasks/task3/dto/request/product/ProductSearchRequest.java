package ru.ylab.tasks.task3.dto.request.product;

import java.math.BigDecimal;

public class ProductSearchRequest {

    private String keyword;
    private String category;
    private String brand;
    private String minPrice;
    private String maxPrice;

    public ProductSearchRequest() {}

    public ProductSearchRequest(String keyword, String category, String brand, String minPrice, String maxPrice) {
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

    public String getMinPrice() {
        return minPrice;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

}
