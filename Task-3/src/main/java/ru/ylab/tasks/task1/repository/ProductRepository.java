package ru.ylab.tasks.task1.repository;

import ru.ylab.tasks.task1.model.Product;

import java.math.BigDecimal;
import java.util.*;

/**
 * Репозиторий для управления товарами.
 * Определяет базовые операции CRUD и методы поиска по различным параметрам.
 */
public interface ProductRepository {
    /**
     * Сохраняет новый товар или обновляет существующий.
     * @param product товар для сохранения
     */
    void save(Product product);

    /**
     * Возвращает все товары.
     * @return коллекция всех товаров
     */
    Collection<Product> findAll();

    /**
     * Находит товар по его идентификатору.
     * @param id идентификатор товара
     * @return найденный товар или {@code null}, если не найден
     */
    Optional<Product> findById(Long id);

    /**
     * Удаляет товар по идентификатору.
     * @param id идентификатор товара
     */
    void deleteById(Long id);

    /**
     * Находит товары по категории.
     * @param category название категории
     * @return коллекция товаров из заданной категории
     */
    Collection<Product> findByCategory(String category);

    /**
     * Находит товары по бренду.
     * @param brand название бренда
     * @return коллекция товаров данного бренда
     */
    Collection<Product> findByBrand(String brand);

    /**
     * Находит товары в указанном диапазоне цен.
     * @param min минимальная цена (включительно)
     * @param max максимальная цена (включительно)
     * @return коллекция товаров, удовлетворяющих диапазону
     */
    Collection<Product> findByPriceRange(BigDecimal min, BigDecimal max);

    /**
     * Возвращает минимальную цену среди всех товаров.
     * @return минимальная цена, если товары есть
     */
    Optional<BigDecimal> getMinPrice();

    /**
     * Возвращает максимальную цену среди всех товаров.
     * @return максимальная цена, если товары есть
     */
    Optional<BigDecimal> getMaxPrice();
}
