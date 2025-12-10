package ru.ylab.tasks.task5.dto.request.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для запроса на поиск продуктов.
 * Содержит критерии фильтрации для поиска продуктов.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchRequest {

    private String keyword;
    private String category;
    private String brand;
    private String minPrice;
    private String maxPrice;

}
