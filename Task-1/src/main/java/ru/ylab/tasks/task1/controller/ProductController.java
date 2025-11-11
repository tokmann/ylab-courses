package ru.ylab.tasks.task1.controller;

import ru.ylab.tasks.task1.constant.Role;
import ru.ylab.tasks.task1.model.Product;
import ru.ylab.tasks.task1.model.User;
import ru.ylab.tasks.task1.security.AuthService;
import ru.ylab.tasks.task1.service.AuditService;
import ru.ylab.tasks.task1.service.ProductService;
import ru.ylab.tasks.task1.util.SearchFilter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Контроллер управления товарами.
 * Делегирует операции {@link ProductService} и фиксирует события через {@link AuditService}.
 */
public class ProductController {

    private final ProductService productService;
    private final AuthService authService;
    private final AuditService audit;

    public ProductController(ProductService productService, AuthService authService, AuditService audit) {
        this.productService = productService;
        this.authService = authService;
        this.audit = audit;
    }

    public void addProduct(Product product) {
        productService.create(product);
        audit.log("Добавлен товар: " + product.getName());
    }

    public void updateProduct(UUID id, String name, String category, String brand, BigDecimal price, String desc) {
        productService.update(id, name, category, brand, price, desc);
        audit.log("Изменен товар: " + id);
    }

    public void deleteProduct(UUID id) {
        productService.delete(id);
        audit.log("Удален товар: " + id);
    }

    public List<Product> getAllProducts() {
        return productService.getAll();
    }

    public List<Product> searchProducts(SearchFilter filter) {
        List<Product> res = productService.search(filter);
        audit.log("Поиск товаров");
        return res;
    }
}
