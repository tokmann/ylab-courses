package ru.ylab.tasks.task5.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для ответа на успешное удаление продукта.
 * Содержит идентификатор удаленного продукта и сообщение.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDeletedResponse {

    private Long productId;
    private String message;

}
