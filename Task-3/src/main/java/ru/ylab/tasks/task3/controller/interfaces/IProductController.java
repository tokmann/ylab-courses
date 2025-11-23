package ru.ylab.tasks.task3.controller.interfaces;

import ru.ylab.tasks.task3.model.Product;
import ru.ylab.tasks.task3.service.product.ProductService;
import ru.ylab.tasks.task3.util.SearchFilter;

import java.math.BigDecimal;
import java.util.List;

public interface IProductController {

    void addProduct(Product product);
    void updateProduct(Long id, String name, String category, String brand, BigDecimal price, String desc);
    void deleteProduct(Long id);
    List<Product> getAllProducts();
    List<Product> searchProducts(SearchFilter filter);
}
