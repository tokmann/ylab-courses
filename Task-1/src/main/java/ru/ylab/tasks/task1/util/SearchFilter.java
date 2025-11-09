package ru.ylab.tasks.task1.util;

import java.math.BigDecimal;

public class SearchFilter {

    public String keyword;
    public String category;
    public String brand;
    public BigDecimal minPrice;
    public BigDecimal maxPrice;

    public SearchFilter(String keyword, String category, String brand, BigDecimal minPrice, BigDecimal maxPrice) {
        this.keyword = keyword;
        this.category = category;
        this.brand = brand;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }
}
