package ru.ylab.tasks.task5.dto.request.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для запроса на удаление продукта.
 * Содержит идентификатор продукта для удаления.
 */
@Data
@NoArgsConstructor
public class ProductDeleteRequest {

    private Long id;

}
