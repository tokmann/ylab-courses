package ru.ylab.tasks.task1.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Модель товара.
 * Содержит основную информацию о продукте и поддерживает обновление данных.
 */
public class Product {

    private final UUID id;
    private String name;
    private String category;
    private String brand;
    private BigDecimal price;
    private String description;

    public Product(String name, String category, String brand, BigDecimal price, String description) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.description = description;
    }

    /**
     * Конструктор для продукта с уже существующим UUID.
     * Используется при загрузке из файла.
     */
    public Product(UUID id, String name, String category, String brand, BigDecimal price, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.description = description;
    }

    public UUID getId() {
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

    public BigDecimal getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
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
