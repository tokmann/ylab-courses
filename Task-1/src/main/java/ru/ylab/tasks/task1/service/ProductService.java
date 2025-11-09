package ru.ylab.tasks.task1.service;

import ru.ylab.tasks.task1.model.Product;
import ru.ylab.tasks.task1.repository.ProductRepository;
import ru.ylab.tasks.task1.util.FilterKey;
import ru.ylab.tasks.task1.util.SearchFilter;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class ProductService {

    private final ProductRepository repo = new ProductRepository();
    private final LruCache<FilterKey, List<Product>> cache = new LruCache<>(50);

    public void create(Product p) {
        repo.save(p);
        cache.clear();
    }

    public void update(UUID id, String name, String cat, String brand, BigDecimal price, String desc) {
        Product p = repo.findById(id);
        if (p != null) {
            repo.delete(id);
            p.update(name, cat, brand, price, desc);
            repo.save(p);
            cache.clear();
        }
    }

    public void delete(UUID id) {
        repo.delete(id);
        cache.clear();
    }

    public List<Product> getAll() {
        return new ArrayList<>(repo.findAll());
    }

    public List<Product> search(SearchFilter f) {
        FilterKey key = new FilterKey(f);
        List<Product> cached = cache.get(key);
        if (cached != null) return cached;

        Set<UUID> result = new HashSet<>(repo.findAll().stream().map(Product::getId).collect(Collectors.toSet()));

        if (f.category != null)
            result.retainAll(repo.getIndexByCategory().getOrDefault(f.category, Set.of()));

        if (f.brand != null)
            result.retainAll(repo.getIndexByBrand().getOrDefault(f.brand, Set.of()));

        if (f.minPrice != null || f.maxPrice != null) {
            BigDecimal min = f.minPrice != null ? f.minPrice : repo.getPriceIndex().firstKey();
            BigDecimal max = f.maxPrice != null ? f.maxPrice : repo.getPriceIndex().lastKey();
            NavigableMap<BigDecimal, Set<UUID>> sub = repo.getPriceIndex().subMap(min, true, max, true);
            Set<UUID> priceFiltered = sub.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
            result.retainAll(priceFiltered);
        }

        List<Product> filtered = result.stream()
                .map(repo::findById)
                .filter(Objects::nonNull)
                .filter(p -> f.keyword == null ||
                        p.getName().toLowerCase().contains(f.keyword.toLowerCase()) ||
                        p.getDescription().toLowerCase().contains(f.keyword.toLowerCase()) ||
                        p.getCategory().toLowerCase().contains(f.keyword.toLowerCase()) ||
                        p.getBrand().toLowerCase().contains(f.keyword.toLowerCase()))
                .collect(Collectors.toList());

        cache.put(key, filtered);
        return filtered;
    }

    public void saveToFile() {
        repo.saveToFile();
    }

    public void loadFromFile() {
        repo.loadFromFile();
    }
}
