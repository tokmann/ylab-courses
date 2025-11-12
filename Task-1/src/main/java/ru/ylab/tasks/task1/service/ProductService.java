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

        if (f.category != null)
            candidates.retainAll(repo.findByCategory(f.category));

        if (f.brand != null)
            candidates.retainAll(repo.findByBrand(f.brand));

        if (f.minPrice != null || f.maxPrice != null) {
            Optional<BigDecimal> repoMin = repo.getMinPrice();
            Optional<BigDecimal> repoMax = repo.getMaxPrice();

            if (repoMin.isEmpty() || repoMax.isEmpty()) {
                cache.put(key, Collections.emptyList());
                return Collections.emptyList();
            }

            BigDecimal min = f.minPrice != null ? f.minPrice : repoMin.get();
            BigDecimal max = f.maxPrice != null ? f.maxPrice : repoMax.get();

            if (min.compareTo(max) > 0) {
                cache.put(key, Collections.emptyList());
                return Collections.emptyList();
            }

            candidates.retainAll(repo.findByPriceRange(min, max));
        }

        List<Product> filtered = candidates.stream()
                .filter(p -> f.keyword == null ||
                        p.getName().toLowerCase().contains(f.keyword.toLowerCase()) ||
                        p.getDescription().toLowerCase().contains(f.keyword.toLowerCase()) ||
                        p.getCategory().toLowerCase().contains(f.keyword.toLowerCase()) ||
                        p.getBrand().toLowerCase().contains(f.keyword.toLowerCase()))
                .toList();

        cache.put(key, filtered);
        return filtered;
    }
}
