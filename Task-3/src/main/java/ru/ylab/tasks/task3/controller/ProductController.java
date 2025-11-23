package ru.ylab.tasks.task3.controller;

import ru.ylab.tasks.task3.model.Product;
import ru.ylab.tasks.task3.security.AuthService;
import ru.ylab.tasks.task3.service.audit.AuditService;
import ru.ylab.tasks.task3.service.product.ProductService;
import ru.ylab.tasks.task3.util.SearchFilter;

import java.math.BigDecimal;
import java.util.List;

import static ru.ylab.tasks.task3.constant.AuditMessages.*;


/**
 * Контроллер управления товарами.
 * Делегирует операции {@link ProductService} и фиксирует события через {@link AuditService}.
 */
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Добавляет новый товар.
     * Делегирует создание товара {@link ProductService} и логирует событие через {@link AuditService}.
     * @param product товар для добавления
     */
    public void addProduct(Product product) {
        productService.create(product);
    }

    /**
     * Обновляет существующий товар по идентификатору.
     * @param id       id товара
     * @param name     новое название
     * @param category новая категория
     * @param brand    новый бренд
     * @param price    новая цена
     * @param desc     новое описание
     */
    public void updateProduct(Long id, String name, String category, String brand, BigDecimal price, String desc) {
        productService.update(id, name, category, brand, price, desc);
    }

    /**
     * Удаляет товар по идентификатору.
     * @param id UUID товара
     */
    public void deleteProduct(Long id) {
        productService.delete(id);
    }

    /**
     * Возвращает все товары.
     * @return список всех товаров
     */
    public List<Product> getAllProducts() {
        return productService.getAll();
    }

    /**
     * Выполняет поиск товаров по фильтру.
     * Делегирует поиск сервису {@link ProductService} и логирует действие пользователя.
     * @param filter фильтр поиска
     * @return список товаров, удовлетворяющих фильтру
     */
    public List<Product> searchProducts(SearchFilter filter) {
        return productService.search(filter);
    }
}
