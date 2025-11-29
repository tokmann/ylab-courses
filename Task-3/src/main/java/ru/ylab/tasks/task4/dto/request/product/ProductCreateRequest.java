package ru.ylab.tasks.task4.dto.request.product;


/**
 * DTO для запроса на создание продукта.
 * Содержит данные, необходимые для создания нового продукта.
 */
public class ProductCreateRequest {

    private String name;
    private String category;
    private String brand;
    private String price;
    private String description;

    public ProductCreateRequest() {}

    public ProductCreateRequest(String name, String category, String brand,
                                String price, String description) {
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.description = description;
    }


    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getBrand() {
        return brand;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }
}
