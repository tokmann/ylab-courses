package ru.ylab.tasks.task4.dto.request.product;

/**
 * DTO для запроса на поиск продуктов.
 * Содержит критерии фильтрации для поиска продуктов.
 */
public class ProductSearchRequest {

    private String keyword;
    private String category;
    private String brand;
    private String minPrice;
    private String maxPrice;

    public ProductSearchRequest() {}

    public ProductSearchRequest(String keyword, String category, String brand, String minPrice, String maxPrice) {
        this.keyword = keyword;
        this.category = category;
        this.brand = brand;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getCategory() {
        return category;
    }

    public String getBrand() {
        return brand;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }
}
