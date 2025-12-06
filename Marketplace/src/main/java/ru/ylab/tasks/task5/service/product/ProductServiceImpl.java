package ru.ylab.tasks.task5.service.product;

import org.springframework.stereotype.Service;
import ru.ylab.tasks.task5.model.Product;
import ru.ylab.tasks.task5.repository.ProductRepository;
import ru.ylab.tasks.task5.util.FilterKey;
import ru.ylab.tasks.task5.util.SearchFilter;
import ru.ylab.tasks.task5.service.performance.LruCache;

import java.math.BigDecimal;
import java.util.*;


/**
 * Сервис для работы с товарами.
 * Содержит бизнес-логику CRUD-операций, поиск с фильтрацией и кеширование результатов поиска.
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;
    private final LruCache<FilterKey, List<Product>> cache = new LruCache<>(50);

    public ProductServiceImpl(ProductRepository repository) {
        this.repo = repository;
    }

    /**
     * Создает новый товар и очищает кеш.
     * @param p объект товара для добавления
     */
    @Override
    public void create(Product p) {
        repo.save(p);
        cache.clear();
    }

    /**
     * Обновляет существующий товар по идентификатору.
     * @param id     идентификатор товара
     * @param name   новое название
     * @param cat    новая категория
     * @param brand  новый бренд
     * @param price  новая цена
     * @param desc   новое описание
     */
    @Override
    public void update(Long id, String name, String cat, String brand, BigDecimal price, String desc) {
        Optional<Product> opt = repo.findById(id);
        if (opt.isPresent()) {
            Product p = opt.get();
            p.update(name, cat, brand, price, desc);
            repo.save(p);
            cache.clear();
        }
    }

    /**
     * Удаляет товар по ID и очищает кеш.
     * @param id идентификатор удаляемого товара
     */
    @Override
    public void delete(Long id) {
        repo.deleteById(id);
        cache.clear();
    }

    /**
     * Возвращает список всех товаров в системе.
     * @return список товаров
     */
    @Override
    public List<Product> getAll() {
        return new ArrayList<>(repo.findAll());
    }

    /**
     * Поиск товаров по фильтрам (ключевое слово, категория, бренд, диапазон цен).
     * Использует индексы и кеширование для повышения производительности.
     */
    @Override
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

    /** Фильтрует список по категории. */
    private List<Product> filterByCategory(List<Product> candidates, SearchFilter f) {
        if (f.category() == null || f.category().isBlank()) return candidates;
        return candidates.stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(f.category()))
                .toList();
    }

    /** Фильтрует список по бренду. */
    private List<Product> filterByBrand(List<Product> candidates, SearchFilter f) {
        if (f.brand() == null || f.brand().isBlank()) return candidates;
        return candidates.stream()
                .filter(p -> p.getBrand().equalsIgnoreCase(f.brand()))
                .toList();
    }

    /** Фильтрует список по диапазону цен с учетом min/max значений. */
    private List<Product> filterByPriceRange(List<Product> candidates, SearchFilter f) {
        if (f.minPrice() == null && f.maxPrice() == null) return candidates;

        Optional<BigDecimal> repoMin = repo.getMinPrice();
        Optional<BigDecimal> repoMax = repo.getMaxPrice();

        if (repoMin.isEmpty() || repoMax.isEmpty()) {
            return Collections.emptyList();
        }

        BigDecimal min = f.minPrice() != null ? f.minPrice() : repoMin.get();
        BigDecimal max = f.maxPrice() != null ? f.maxPrice() : repoMax.get();

        if (min.compareTo(max) > 0) {
            return Collections.emptyList();
        }

        return candidates.stream()
                .filter(p -> p.getPrice().compareTo(min) >= 0 && p.getPrice().compareTo(max) <= 0)
                .toList();
    }

    /** Фильтрует список по ключевому слову (поиск по названию, описанию, категории и бренду). */
    private List<Product> filterByKeyword(List<Product> candidates, SearchFilter f) {
        if (f.keyword() == null || f.keyword().isBlank()) return candidates;

        String keyword = f.keyword().toLowerCase();

        return candidates.stream()
                .filter(p -> p.getName().toLowerCase().contains(keyword)
                        || p.getDescription().toLowerCase().contains(keyword)
                        || p.getCategory().toLowerCase().contains(keyword)
                        || p.getBrand().toLowerCase().contains(keyword))
                .toList();
    }

}
