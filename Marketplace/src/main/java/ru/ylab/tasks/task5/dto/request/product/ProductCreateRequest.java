package ru.ylab.tasks.task5.dto.request.product;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для запроса на создание продукта.
 * Содержит данные, необходимые для создания нового продукта.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {

    private String name;
    private String category;
    private String brand;
    private String price;
    private String description;

}

