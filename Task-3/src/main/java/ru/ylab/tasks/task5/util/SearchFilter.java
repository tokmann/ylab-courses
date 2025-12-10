package ru.ylab.tasks.task4.util;

import ru.ylab.tasks.task4.service.product.ProductServiceImpl;

import java.math.BigDecimal;

/**
 * Модель фильтра для поиска товаров.
 * Используется для передачи критериев поиска в {@link ProductServiceImpl} и
 * формирования ключа {@link FilterKey} при кэшировании результатов.
 */
public record SearchFilter (
    String keyword,
    String category,
    String brand,
    BigDecimal minPrice,
    BigDecimal maxPrice

) {}
