package ru.ylab.tasks.task5.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для ответа на успешное обновление продукта.
 * Содержит идентификатор обновленного продукта и сообщение.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdatedResponse {

    private Long productId;
    private String message;

}
