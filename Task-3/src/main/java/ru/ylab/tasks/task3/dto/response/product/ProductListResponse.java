package ru.ylab.tasks.task3.dto.response.product;

import java.util.List;

public class ProductListResponse {

    private List<ProductResponse> products;

    public ProductListResponse(List<ProductResponse> products) {
        this.products = products;
    }

    public List<ProductResponse> getProducts() {
        return products;
    }

    public void setProducts(List<ProductResponse> products) {
        this.products = products;
    }
}

