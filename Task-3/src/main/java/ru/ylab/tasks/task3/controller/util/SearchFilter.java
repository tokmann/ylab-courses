package ru.ylab.tasks.task3.controller.util;

import ru.ylab.tasks.task3.service.ProductService;

import java.math.BigDecimal;

/**
 * Модель фильтра для поиска товаров.
 * Используется для передачи критериев поиска в {@link ProductService} и
 * формирования ключа {@link FilterKey} при кэшировании результатов.
 */
public record SearchFilter (
    String keyword,
    String category,
    String brand,
    BigDecimal minPrice,
    BigDecimal maxPrice

) {}
