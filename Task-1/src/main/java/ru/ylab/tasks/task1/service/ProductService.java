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
        // Генерируем ключ фильтра для кеша
        FilterKey key = new FilterKey(f);

        // Проверяем, есть ли уже готовый результат поиска
        List<Product> cached = cache.get(key);
        if (cached != null) return cached;

        // Начинаем с полного набора ID всех товаров
        Set<UUID> result = new HashSet<>(repo.findAll().stream()
                .map(Product::getId)
                .collect(Collectors.toSet()));

        // Фильтрация по категории
        if (f.category != null)
            result.retainAll(repo.getIndexByCategory().getOrDefault(f.category, Set.of()));

        // Фильтрация по бренду
        if (f.brand != null)
            result.retainAll(repo.getIndexByBrand().getOrDefault(f.brand, Set.of()));

        // Фильтрация по диапазону цен
        if (f.minPrice != null || f.maxPrice != null) {
            BigDecimal min = f.minPrice != null ? f.minPrice : repo.getPriceIndex().firstKey();
            BigDecimal max = f.maxPrice != null ? f.maxPrice : repo.getPriceIndex().lastKey();

            // Получаем поддиапазон из индексной структуры по ценам
            NavigableMap<BigDecimal, Set<UUID>> sub = repo.getPriceIndex().subMap(min, true, max, true);
            Set<UUID> priceFiltered = sub.values().stream()
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());

            result.retainAll(priceFiltered);
        }

        // Преобразуем ID обратно в объекты Product и фильтруем по ключевому слову
        List<Product> filtered = result.stream()
                .map(repo::findById)
                .filter(Objects::nonNull)
                .filter(p -> f.keyword == null ||
                        p.getName().toLowerCase().contains(f.keyword.toLowerCase()) ||
                        p.getDescription().toLowerCase().contains(f.keyword.toLowerCase()) ||
                        p.getCategory().toLowerCase().contains(f.keyword.toLowerCase()) ||
                        p.getBrand().toLowerCase().contains(f.keyword.toLowerCase()))
                .collect(Collectors.toList());

        // Сохраняем результат поиска в кеш
        cache.put(key, filtered);
        return filtered;
    }
}
