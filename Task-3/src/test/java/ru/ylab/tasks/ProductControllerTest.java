package ru.ylab.tasks;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ylab.tasks.task3.constant.Role;
import ru.ylab.tasks.task3.controller.ProductController;
import ru.ylab.tasks.task3.model.Product;
import ru.ylab.tasks.task3.model.User;
import ru.ylab.tasks.task3.repository.jdbc.JdbcProductRepository;
import ru.ylab.tasks.task3.repository.jdbc.JdbcUserRepository;
import ru.ylab.tasks.task3.security.AuthService;
import ru.ylab.tasks.task3.service.audit.AuditService;
import ru.ylab.tasks.task3.service.product.ProductService;
import ru.ylab.tasks.task3.util.SearchFilter;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductControllerTest {

    private ProductController productController;
    private AuthService authService;
    private AuditService auditService;

    @BeforeEach
    void setUp() {
        TestDatabase.clearData();
        JdbcProductRepository productRepo = new JdbcProductRepository(TestDatabase.getConnection());
        JdbcUserRepository userRepo = new JdbcUserRepository(TestDatabase.getConnection());

        ProductService productService = new ProductService(productRepo);
        authService = new AuthService(userRepo);
        auditService = new AuditService();

        productController = new ProductController(productService, authService, auditService);

        User admin = new User("admin", "adminpass", Role.ADMIN);
        userRepo.save(admin);
        authService.login("admin", "adminpass");
    }

    @Test
    void addProduct_shouldAddProductAndLogAction() {
        Product product = new Product("New Product", "Category", "Brand",
                new BigDecimal("100"), "Description");

        productController.addProduct(product);

        List<Product> products = productController.getAllProducts();
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("New Product");
    }

    @Test
    void updateProduct_shouldUpdateProductAndLogAction() {
        Product product = new Product("Original", "Cat", "Brand", new BigDecimal("50"), "Desc");
        productController.addProduct(product);
        Long id = product.getId();

        productController.updateProduct(id, "Updated", "NewCat", "NewBrand",
                new BigDecimal("75"), "NewDesc");

        List<Product> products = productController.getAllProducts();
        assertThat(products.get(0).getName()).isEqualTo("Updated");
        assertThat(products.get(0).getPrice()).isEqualTo(new BigDecimal("75.00"));
    }

    @Test
    void deleteProduct_shouldRemoveProductAndLogAction() {
        Product product = new Product("ToDelete", "Cat", "Brand", new BigDecimal("50"), "Desc");
        productController.addProduct(product);
        Long id = product.getId();

        productController.deleteProduct(id);

        List<Product> products = productController.getAllProducts();
        assertThat(products).isEmpty();
    }

    @Test
    void getAllProducts_shouldReturnAllProducts() {
        productController.addProduct(new Product("P1", "Cat1", "Brand1", new BigDecimal("10"), "Desc1"));
        productController.addProduct(new Product("P2", "Cat2", "Brand2", new BigDecimal("20"), "Desc2"));

        List<Product> products = productController.getAllProducts();

        assertThat(products).hasSize(2);
    }

    @Test
    void searchProducts_shouldReturnFilteredResultsAndLogAction() {
        productController.addProduct(new Product("Laptop", "Electronics", "Dell",
                new BigDecimal("1000"), "Gaming"));
        productController.addProduct(new Product("Book", "Books", "Penguin",
                new BigDecimal("20"), "Novel"));

        var filter = new SearchFilter(null, "Electronics", null, null, null);

        List<Product> results = productController.searchProducts(filter);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getCategory()).isEqualTo("Electronics");
    }
}
