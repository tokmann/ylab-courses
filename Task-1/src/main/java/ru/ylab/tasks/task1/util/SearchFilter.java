package ru.ylab.tasks.task1.util;

import ru.ylab.tasks.task1.service.ProductService;

import java.math.BigDecimal;

/**
 * Модель фильтра для поиска товаров.
 * Используется для передачи критериев поиска в {@link ProductService} и
 * формирования ключа {@link FilterKey} при кэшировании результатов.
 */
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
