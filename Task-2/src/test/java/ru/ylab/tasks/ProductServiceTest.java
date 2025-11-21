package ru.ylab.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ylab.tasks.task2.model.Product;
import ru.ylab.tasks.task2.repository.jdbc.JdbcProductRepository;
import ru.ylab.tasks.task2.service.ProductService;
import ru.ylab.tasks.task2.util.SearchFilter;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductServiceTest {

    private ProductService productService;
    private JdbcProductRepository productRepository;

    @BeforeEach
    void setUp() {
        TestDatabase.clearData();
        productRepository = new JdbcProductRepository(TestDatabase.getConnection());
        productService = new ProductService(productRepository);

        productService.create(new Product("Laptop", "Electronics", "Dell",
                new BigDecimal("1000"), "Gaming laptop"));
        productService.create(new Product("Book", "Books", "Penguin",
                new BigDecimal("20"), "Novel"));
        productService.create(new Product("Phone", "Electronics", "Samsung",
                new BigDecimal("500"), "Smartphone"));
    }

    @Test
    void create_shouldAddNewProduct() {
        Product newProduct = new Product("Tablet", "Electronics", "Apple",
                new BigDecimal("300"), "iPad");

        productService.create(newProduct);

        List<Product> allProducts = productService.getAll();
        assertThat(allProducts).hasSize(4);
        assertThat(allProducts).extracting(Product::getName).contains("Tablet");
    }

    @Test
    void update_shouldModifyExistingProduct() {
        List<Product> products = productService.getAll();
        Long id = products.get(0).getId();

        productService.update(id, "Updated Laptop", "Computers", "Dell",
                new BigDecimal("1200"), "Updated description");

        var updated = productRepository.findById(id);
        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("Updated Laptop");
        assertThat(updated.get().getCategory()).isEqualTo("Computers");
        assertThat(updated.get().getPrice()).isEqualTo(new BigDecimal("1200.00"));
    }

    @Test
    void delete_shouldRemoveProduct() {
        List<Product> products = productService.getAll();
        Long id = products.get(0).getId();

        productService.delete(id);

        var deleted = productRepository.findById(id);
        assertThat(deleted).isEmpty();
    }

    @Test
    void getAll_shouldReturnAllProducts() {
        List<Product> products = productService.getAll();

        assertThat(products).hasSize(3);
    }

    @Test
    void search_shouldFilterByCategory() {
        var filter = new SearchFilter(null, "Electronics", null, null, null);

        List<Product> results = productService.search(filter);

        assertThat(results).hasSize(2);
        assertThat(results).allMatch(p -> p.getCategory().equals("Electronics"));
    }

    @Test
    void search_shouldFilterByBrand() {
        var filter = new SearchFilter(null, null, "Samsung", null, null);

        List<Product> results = productService.search(filter);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getBrand()).isEqualTo("Samsung");
    }

    @Test
    void search_shouldFilterByKeyword() {
        var filter = new SearchFilter("laptop", null, null, null, null);

        List<Product> results = productService.search(filter);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Laptop");
    }

    @Test
    void search_shouldFilterByPriceRange() {
        var filter = new SearchFilter(null, null, null,
                new BigDecimal("100"), new BigDecimal("600"));

        List<Product> results = productService.search(filter);

        assertThat(results).hasSize(1);
        assertThat(results).allMatch(p ->
                p.getPrice().compareTo(new BigDecimal("100")) >= 0 &&
                        p.getPrice().compareTo(new BigDecimal("600")) <= 0);
    }

    @Test
    void search_shouldUseCacheForSameFilter() {
        var filter = new SearchFilter(null, "Electronics", null, null, null);

        List<Product> firstResults = productService.search(filter);

        List<Product> secondResults = productService.search(filter);

        assertThat(secondResults).isEqualTo(firstResults);
    }
}
