package ru.ylab.tasks.task5.dto.response.product;

/**
 * DTO для ответа на успешное обновление продукта.
 * Содержит идентификатор обновленного продукта и сообщение.
 */
public class ProductUpdatedResponse {

    private Long productId;
    private String message;

    public ProductUpdatedResponse() {}

    public ProductUpdatedResponse(Long productId, String message) {
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
