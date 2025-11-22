package ru.ylab.tasks.task3.dto.response.product;

import java.util.List;

public class ProductListResponse {

    private List<ProductResponse> products;

    public ProductListResponse(List<ProductResponse> products) {
        this.products = products;
    }
}

