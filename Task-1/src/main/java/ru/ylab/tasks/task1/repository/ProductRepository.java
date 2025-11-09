package ru.ylab.tasks.task1.repository;

import ru.ylab.tasks.task1.model.Product;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

public class ProductRepository {

    private final Map<UUID, Product> productsById = new HashMap<>();
    private final Map<String, Set<UUID>> indexByCategory = new HashMap<>();
    private final Map<String, Set<UUID>> indexByBrand = new HashMap<>();
    private final TreeMap<BigDecimal, Set<UUID>> priceIndex = new TreeMap<>();

    private final String FILE_NAME = "products.txt";

    public void save(Product p) {
        productsById.put(p.getId(), p);
        index(indexByCategory, p.getCategory(), p.getId());
        index(indexByBrand, p.getBrand(), p.getId());
        index(priceIndex, p.getPrice(), p.getId());
    }

    public Product findById(UUID id) {
        return productsById.get(id);
    }

    public void delete(UUID id) {
        Product p = productsById.remove(id);
        if (p == null) return;
        remove(indexByCategory, p.getCategory(), id);
        remove(indexByBrand, p.getBrand(), id);
        remove(priceIndex, p.getPrice(), id);
    }

    public Collection<Product> findAll() {
        return productsById.values();
    }

    public void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 6) continue;
                Product p = new Product(
                        UUID.fromString(parts[0]),
                        parts[1],
                        parts[2],
                        parts[3],
                        new BigDecimal(parts[4]),
                        parts[5]
                );
                save(p);
            }
        } catch (IOException e) {
            System.out.println("Ошибка загрузки продуктов: " + e.getMessage());
        }
    }

    public void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Product p : productsById.values()) {
                writer.write(p.getId() + "|" + p.getName() + "|" + p.getCategory() + "|" + p.getBrand() + "|" + p.getPrice() + "|" + p.getDescription().replace("\n", " "));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Ошибка сохранения продуктов: " + e.getMessage());
        }
    }

    private <K> void index(Map<K, Set<UUID>> map, K key, UUID id) {
        map.computeIfAbsent(key, k -> new HashSet<>()).add(id);
    }

    private <K> void remove(Map<K, Set<UUID>> map, K key, UUID id) {
        Set<UUID> set = map.get(key);
        if (set != null) {
            set.remove(id);
            if (set.isEmpty()) map.remove(key);
        }
    }

    public Map<String, Set<UUID>> getIndexByCategory() {
        return indexByCategory;
    }

    public Map<String, Set<UUID>> getIndexByBrand() {
        return indexByBrand;
    }

    public TreeMap<BigDecimal, Set<UUID>> getPriceIndex() {
        return priceIndex;
    }

}
