package ru.ylab.tasks.task3.dto.response.product;

/**
 * DTO для ответа на успешное создание продукта.
 * Содержит идентификатор созданного продукта и сообщение.
 */
public class ProductCreatedResponse {

    private Long productId;
    private String message;

    public ProductCreatedResponse() {}

    public ProductCreatedResponse(Long productId, String message) {
        this.productId = productId;
        this.message = message;
    }

    public Long getProductId() {
        return productId;
    }

    public String getMessage() {
        return message;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
