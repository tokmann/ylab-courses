package ru.ylab.tasks.task3.controller;

import ru.ylab.tasks.task3.controller.interfaces.IProductController;
import ru.ylab.tasks.task3.model.Product;
import ru.ylab.tasks.task3.service.audit.AuditServiceImpl;
import ru.ylab.tasks.task3.service.product.IProductService;
import ru.ylab.tasks.task3.service.product.ProductServiceImpl;
import ru.ylab.tasks.task3.util.SearchFilter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Контроллер управления товарами.
 * Делегирует операции {@link ProductServiceImpl} и фиксирует события через {@link AuditServiceImpl}.
 */
public class ProductControllerImpl implements IProductController {

    private final IProductService productService;

    public ProductControllerImpl(IProductService productService) {
        this.productService = productService;
    }

    /**
     * Добавляет новый товар.
     * Делегирует создание товара {@link ProductServiceImpl} и логирует событие через {@link AuditServiceImpl}.
     * @param product товар для добавления
     */
    @Override
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
    @Override
    public void updateProduct(Long id, String name, String category, String brand, BigDecimal price, String desc) {
        productService.update(id, name, category, brand, price, desc);
    }

    /**
     * Удаляет товар по идентификатору.
     * @param id UUID товара
     */
    @Override
    public void deleteProduct(Long id) {
        productService.delete(id);
    }

    /**
     * Возвращает все товары.
     * @return список всех товаров
     */
    @Override
    public List<Product> getAllProducts() {
        return productService.getAll();
    }

    /**
     * Выполняет поиск товаров по фильтру.
     * Делегирует поиск сервису {@link ProductServiceImpl} и логирует действие пользователя.
     * @param filter фильтр поиска
     * @return список товаров, удовлетворяющих фильтру
     */
    @Override
    public List<Product> searchProducts(SearchFilter filter) {
        return productService.search(filter);
    }
}
