package ru.ylab.tasks.task1.repository;

import ru.ylab.tasks.task1.model.Product;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * Репозиторий товаров.
 * Хранит продукты в памяти и поддерживает индексы по категориям, брендам и ценам.
 * Обеспечивает быстрое чтение и поиск.
 */
public class InMemoryProductRepository implements ProductRepository {

    private final Map<UUID, Product> productsById = new HashMap<>();
    private final Map<String, Set<UUID>> indexByCategory = new HashMap<>();
    private final Map<String, Set<UUID>> indexByBrand = new HashMap<>();
    private final TreeMap<BigDecimal, Set<UUID>> priceIndex = new TreeMap<>();

    public InMemoryProductRepository(Collection<Product> initialProducts) {
        initialProducts.forEach(this::save);
    }

    @Override
    public void save(Product p) {
        productsById.put(p.getId(), p);
        index(indexByCategory, p.getCategory(), p.getId());
        index(indexByBrand, p.getBrand(), p.getId());
        index(priceIndex, p.getPrice(), p.getId());
    }

    @Override
    public Product findById(UUID id) {
        return productsById.get(id);
    }

    @Override
    public void delete(UUID id) {
        Product p = productsById.remove(id);
        if (p == null) return;
        remove(indexByCategory, p.getCategory(), id);
        remove(indexByBrand, p.getBrand(), id);
        remove(priceIndex, p.getPrice(), id);
    }

    @Override
    public Collection<Product> findAll() {
        return productsById.values();
    }

    /** Добавляет ID продукта в индекс по ключу */
    private <K> void index(Map<K, Set<UUID>> map, K key, UUID id) {
        map.computeIfAbsent(key, k -> new HashSet<>()).add(id);
    }

    /** Удаляет ID продукта из индекса */
    private <K> void remove(Map<K, Set<UUID>> map, K key, UUID id) {
        Set<UUID> set = map.get(key);
        if (set != null) {
            set.remove(id);
            if (set.isEmpty()) map.remove(key);
        }
    }

    // Геттеры для индексов
    @Override
    public Map<String, Set<UUID>> getIndexByCategory() {
        return indexByCategory;
    }

    @Override
    public Map<String, Set<UUID>> getIndexByBrand() {
        return indexByBrand;
    }

    @Override
    public TreeMap<BigDecimal, Set<UUID>> getPriceIndex() {
        return priceIndex;
    }

}
