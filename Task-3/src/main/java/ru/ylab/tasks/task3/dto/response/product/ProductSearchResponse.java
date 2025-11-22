package ru.ylab.tasks.task3.dto.response.product;

import java.util.List;

public class ProductSearchResponse {

    private List<ProductResponse> results;

    public ProductSearchResponse(List<ProductResponse> results) {
        this.results = results;
    }

}

