package ru.ylab.tasks.task1.repository;

import ru.ylab.tasks.task1.model.Product;

import java.math.BigDecimal;
import java.util.*;

public interface ProductRepository {
    void save(Product product);
    Collection<Product> findAll();
    Product findById(UUID id);
    void delete(UUID id);
    Map<String, Set<UUID>> getIndexByCategory();
    Map<String, Set<UUID>> getIndexByBrand();
    NavigableMap<BigDecimal, Set<UUID>> getPriceIndex();
}
