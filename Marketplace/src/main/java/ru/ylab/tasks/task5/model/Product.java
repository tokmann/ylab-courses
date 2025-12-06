package ru.ylab.tasks.task5.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Модель товара.
 * Содержит основную информацию о продукте и поддерживает обновление данных.
 */
public class Product {

    private Long id;
    private String name;
    private String category;
    private String brand;
    private BigDecimal price;
    private String description;

    public Product() {}

    public Product(String name, String category, String brand, BigDecimal price, String description) {
        this.id = null;
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.description = description;
    }

    /**
     * Конструктор для продукта с уже существующим id.
     * Используется при загрузке данных.
     */
    public Product(Long id, String name, String category, String brand, BigDecimal price, String description) {
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

    /**
     * Вызывается только репозиторием.
     */
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {this.category = category; }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) { this.brand = brand; }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Обновляет все поля продукта.
     * Используется при редактировании существующего товара.
     */
    public void update(String name, String category, String brand, BigDecimal price, String description) {
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.description = description;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + name + " (" + brand + ", " + category + ") - " + price + "₽ | " + description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product p = (Product) o;
        return Objects.equals(id, p.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
