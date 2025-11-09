package ru.ylab.tasks.task1.controller;

import ru.ylab.tasks.task1.constant.Role;
import ru.ylab.tasks.task1.model.Product;
import ru.ylab.tasks.task1.model.User;
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
    private final AuditService audit;

    public ProductController(ProductService productService, AuditService audit) {
        this.productService = productService;
        this.audit = audit;
    }

    public void addProduct(String name, String category, String brand, BigDecimal price, String desc) {
        Product p = new Product(name, category, brand, price, desc);
        productService.create(p);
        audit.log("Добавлен товар: " + p.getName());
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

    /**
     * Проверяет, что пользователь — администратор.
     * Если нет, выбрасывает исключение.
     */
    public void checkAdmin(User user) {
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Нет прав для выполнения этой операции. Только ADMIN.");
        }
    }
}
