package ru.ylab.tasks.task3.dto.response.product;

/**
 * DTO для ответа на успешное удаление продукта.
 * Содержит идентификатор удаленного продукта и сообщение.
 */
public class ProductDeletedResponse {

    private Long productId;
    private String message;

    public ProductDeletedResponse() {}

    public ProductDeletedResponse(Long productId, String message) {
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
