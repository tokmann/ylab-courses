package ru.ylab.tasks.task3.util;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Класс-ключ для кэширования результатов поиска товаров.
 * Основан на параметрах фильтра {@link SearchFilter},
 * чтобы кэш возвращал одинаковый результат для одинаковых запросов.
 */
public class FilterKey {

    private final String keyword;
    private final String category;
    private final String brand;
    private final BigDecimal minPrice;
    private final BigDecimal maxPrice;

    /**
     * Создаёт ключ фильтра из объекта {@link SearchFilter}.
     */
    public FilterKey(SearchFilter f) {
        this.keyword = f.keyword();
        this.category = f.category();
        this.brand = f.brand();
        this.minPrice = f.minPrice();
        this.maxPrice = f.maxPrice();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FilterKey)) return false;
        FilterKey k = (FilterKey) o;
        return Objects.equals(keyword, k.keyword)
                && Objects.equals(category, k.category)
                && Objects.equals(brand, k.brand)
                && Objects.equals(minPrice, k.minPrice)
                && Objects.equals(maxPrice, k.maxPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyword, category, brand, minPrice, maxPrice);
    }
}
