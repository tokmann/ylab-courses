package ru.ylab.tasks.task1.repository.inmemory;

import ru.ylab.tasks.task1.model.Product;
import ru.ylab.tasks.task1.repository.ProductRepository;

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

    /**
     * Сохраняет продукт в репозитории и обновляет индексы.
     * @param p продукт для сохранения
     */
    @Override
    public void save(Product p) {
        productsById.put(p.getId(), p);
        index(indexByCategory, p.getCategory(), p.getId());
        index(indexByBrand, p.getBrand(), p.getId());
        index(priceIndex, p.getPrice(), p.getId());
    }

    /**
     * Находит продукт по уникальному идентификатору.
     * @param id UUID продукта
     * @return продукт или null, если не найден
     */
    @Override
    public Product findById(UUID id) {
        return productsById.get(id);
    }

    /**
     * Удаляет продукт из репозитория и обновляет индексы.
     * @param id UUID продукта для удаления
     */
    @Override
    public void delete(UUID id) {
        Product p = productsById.remove(id);
        if (p == null) return;
        remove(indexByCategory, p.getCategory(), id);
        remove(indexByBrand, p.getBrand(), id);
        remove(priceIndex, p.getPrice(), id);
    }

    /**
     * Возвращает все продукты в репозитории.
     * @return коллекция всех продуктов
     */
    @Override
    public Collection<Product> findAll() {
        return productsById.values();
    }

    /**
     * Находит продукты по категории.
     * @param category категория
     * @return коллекция продуктов данной категории
     */
    @Override
    public Collection<Product> findByCategory(String category) {
        Set<UUID> ids = indexByCategory.getOrDefault(category, Set.of());
        return ids.stream()
                .map(productsById::get)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Находит продукты по бренду.
     * @param brand бренд
     * @return коллекция продуктов данного бренда
     */
    @Override
    public Collection<Product> findByBrand(String brand) {
        Set<UUID> ids = indexByBrand.getOrDefault(brand, Set.of());
        return ids.stream()
                .map(productsById::get)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Находит продукты в указанном диапазоне цен.
     * @param min минимальная цена
     * @param max максимальная цена
     * @return коллекция продуктов в диапазоне
     */
    @Override
    public Collection<Product> findByPriceRange(BigDecimal min, BigDecimal max) {
        NavigableMap<BigDecimal, Set<UUID>> sub = priceIndex.subMap(min, true, max, true);
        return sub.values().stream()
                .flatMap(Set::stream)
                .map(productsById::get)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Возвращает минимальную цену среди всех продуктов.
     * @return минимальная цена или пустой Optional, если продуктов нет
     */
    @Override
    public Optional<BigDecimal> getMinPrice() {
        if (priceIndex.isEmpty()) return Optional.empty();
        return Optional.of(priceIndex.firstKey());
    }

    /**
     * Возвращает максимальную цену среди всех продуктов.
     * @return максимальная цена или пустой Optional, если продуктов нет
     */
    @Override
    public Optional<BigDecimal> getMaxPrice() {
        if (priceIndex.isEmpty()) return Optional.empty();
        return Optional.of(priceIndex.lastKey());
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

}
