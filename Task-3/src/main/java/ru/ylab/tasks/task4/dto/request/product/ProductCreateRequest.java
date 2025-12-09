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

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
