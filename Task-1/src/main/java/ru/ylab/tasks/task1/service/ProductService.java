package ru.ylab.tasks.task1.service;

import ru.ylab.tasks.task1.model.Product;
import ru.ylab.tasks.task1.repository.InMemoryProductRepository;
import ru.ylab.tasks.task1.repository.ProductRepository;
import ru.ylab.tasks.task1.util.FilterKey;
import ru.ylab.tasks.task1.util.SearchFilter;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Сервис для работы с товарами.
 * Содержит бизнес-логику CRUD-операций, поиск с фильтрацией и кеширование результатов поиска.
 */
public class ProductService {

    private final ProductRepository repo;
    private final LruCache<FilterKey, List<Product>> cache = new LruCache<>(50);

    public ProductService(ProductRepository repository) {
        this.repo = repository;
    }

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

    /**
     * Поиск товаров по фильтрам (ключевое слово, категория, бренд, диапазон цен).
     * Использует индексы и кеширование для повышения производительности.
     */
    public List<Product> search(SearchFilter f) {
        FilterKey key = new FilterKey(f);
        List<Product> cached = cache.get(key);
        if (cached != null) return cached;

        List<Product> candidates = new ArrayList<>(repo.findAll());

        candidates = filterByCategory(candidates, f);
        candidates = filterByBrand(candidates, f);
        candidates = filterByPriceRange(candidates, f);
        candidates = filterByKeyword(candidates, f);

        cache.put(key, candidates);
        return candidates;
    }

    private List<Product> filterByCategory(List<Product> candidates, SearchFilter f) {
        if (f.category == null) return candidates;
        return candidates.stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(f.category))
                .toList();
    }

    private List<Product> filterByBrand(List<Product> candidates, SearchFilter f) {
        if (f.brand == null) return candidates;
        return candidates.stream()
                .filter(p -> p.getBrand().equalsIgnoreCase(f.brand))
                .toList();
    }

    private List<Product> filterByPriceRange(List<Product> candidates, SearchFilter f) {
        if (f.minPrice == null && f.maxPrice == null) return candidates;

        Optional<BigDecimal> repoMin = repo.getMinPrice();
        Optional<BigDecimal> repoMax = repo.getMaxPrice();

        if (repoMin.isEmpty() || repoMax.isEmpty()) {
            return Collections.emptyList();
        }

        BigDecimal min = f.minPrice != null ? f.minPrice : repoMin.get();
        BigDecimal max = f.maxPrice != null ? f.maxPrice : repoMax.get();

        if (min.compareTo(max) > 0) {
            return Collections.emptyList();
        }

        return candidates.stream()
                .filter(p -> p.getPrice().compareTo(min) >= 0 && p.getPrice().compareTo(max) <= 0)
                .toList();
    }

    private List<Product> filterByKeyword(List<Product> candidates, SearchFilter f) {
        if (f.keyword == null || f.keyword.isBlank()) return candidates;

        String keyword = f.keyword.toLowerCase();

        return candidates.stream()
                .filter(p -> p.getName().toLowerCase().contains(keyword)
                        || p.getDescription().toLowerCase().contains(keyword)
                        || p.getCategory().toLowerCase().contains(keyword)
                        || p.getBrand().toLowerCase().contains(keyword))
                .toList();
    }

}
