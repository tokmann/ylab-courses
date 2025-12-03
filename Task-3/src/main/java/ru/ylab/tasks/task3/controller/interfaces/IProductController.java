package ru.ylab.tasks.task3.controller.interfaces;

import ru.ylab.tasks.task3.model.Product;
import ru.ylab.tasks.task3.util.SearchFilter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Интерфейс контроллера для управления продуктами.
 * Определяет основные операции CRUD для продуктов.
 */
public interface IProductController {

    /**
     * Добавляет новый продукт в систему.
     * @param product объект продукта для добавления
     */
    void addProduct(Product product);

    /**
     * Обновляет существующий продукт.
     * @param id идентификатор продукта для обновления
     * @param name новое название продукта
     * @param category новая категория продукта
     * @param brand новый бренд продукта
     * @param price новая цена продукта
     * @param desc новое описание продукта
     */
    void updateProduct(Long id, String name, String category, String brand, BigDecimal price, String desc);

    /**
     * Удаляет продукт по идентификатору.
     * @param id идентификатор продукта для удаления
     */
    void deleteProduct(Long id);

    /**
     * Возвращает список всех продуктов.
     * @return список всех продуктов в системе
     */
    List<Product> getAllProducts();

    /**
     * Выполняет поиск продуктов по заданным фильтрам.
     * @param filter фильтры для поиска продуктов
     * @return список продуктов, соответствующих критериям поиска
     */
    List<Product> searchProducts(SearchFilter filter);
}
