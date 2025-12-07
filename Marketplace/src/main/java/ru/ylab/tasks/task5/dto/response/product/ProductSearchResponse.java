package ru.ylab.tasks.task5.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO для ответа на запрос поиска продуктов.
 * Содержит результаты поиска продуктов.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSearchResponse {

    private List<ProductResponse> results;

}

