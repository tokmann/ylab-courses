package ru.ylab.tasks.task5.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для ответа на успешное создание продукта.
 * Содержит идентификатор созданного продукта и сообщение.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreatedResponse {

    private Long productId;
    private String message;

}
