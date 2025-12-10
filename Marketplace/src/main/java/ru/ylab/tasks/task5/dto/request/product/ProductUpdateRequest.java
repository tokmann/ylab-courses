package ru.ylab.tasks.task5.dto.request.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для запроса на обновление продукта.
 * Содержит обновленные данные продукта.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {

    private Long id;
    private String name;
    private String category;
    private String brand;
    private String price;
    private String description;

}
