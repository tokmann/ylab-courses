package ru.ylab.tasks.task3.dto.request.product;


public class ProductUpdateRequest {

    private final Long id;
    private final String name;
    private final String category;
    private final String brand;
    private final String price;
    private final String description;

    public ProductUpdateRequest(Long id, String name, String category,
                                String brand, String price, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.description = description;
    }

    public Long getId() {
        return id;
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
