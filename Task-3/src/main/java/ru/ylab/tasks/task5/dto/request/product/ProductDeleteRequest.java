package ru.ylab.tasks.task4.dto.request.product;

/**
 * DTO для запроса на удаление продукта.
 * Содержит идентификатор продукта для удаления.
 */
public class ProductDeleteRequest {

    private Long id;

    public ProductDeleteRequest() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
