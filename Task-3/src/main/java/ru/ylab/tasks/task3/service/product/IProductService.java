package ru.ylab.tasks.task3.service.product;

import ru.ylab.tasks.task3.model.Product;
import ru.ylab.tasks.task3.util.SearchFilter;

import java.math.BigDecimal;
import java.util.List;

public interface IProductService {

    /**
     * Создаёт новый товар.
     */
    void create(Product product);

    /**
     * Обновляет существующий товар.
     */
    void update(Long id, String name, String category, String brand, BigDecimal price, String description);

    /**
     * Удаляет товар по идентификатору.
     */
    void delete(Long id);

    /**
     * Возвращает все товары.
     */
    List<Product> getAll();

    /**
     * Выполняет поиск товаров по заданному фильтру.
     */
    List<Product> search(SearchFilter filter);
}
