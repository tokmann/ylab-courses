package ru.ylab.tasks.task4.dto.response.product;

import java.util.List;

/**
 * DTO для ответа на запрос поиска продуктов.
 * Содержит результаты поиска продуктов.
 */
public class ProductSearchResponse {

    private List<ProductResponse> results;

    public ProductSearchResponse(List<ProductResponse> results) {
        this.results = results;
    }

    public List<ProductResponse> getResults() {
        return results;
    }

    public void setResults(List<ProductResponse> results) {
        this.results = results;
    }
}

