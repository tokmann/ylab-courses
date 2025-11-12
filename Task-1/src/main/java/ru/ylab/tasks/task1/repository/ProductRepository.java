package ru.ylab.tasks.task1.repository;

import ru.ylab.tasks.task1.model.Product;

import java.math.BigDecimal;
import java.util.*;

public interface ProductRepository {
    void save(Product product);
    Collection<Product> findAll();
    Product findById(UUID id);
    void delete(UUID id);
    Collection<Product> findByCategory(String brand);
    Collection<Product> findByBrand(String brand);
    Collection<Product> findByPriceRange(BigDecimal min, BigDecimal max);
    Optional<BigDecimal> getMinPrice();
    Optional<BigDecimal> getMaxPrice();
}
