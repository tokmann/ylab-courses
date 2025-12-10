package ru.ylab.tasks.task5.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO для ответа с информацией о продукте.
 * Содержит полную информацию о продукте.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String name;
    private String category;
    private String brand;
    private BigDecimal price;
    private String description;

}
