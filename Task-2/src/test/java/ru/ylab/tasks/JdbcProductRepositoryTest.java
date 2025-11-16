package ru.ylab.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ylab.tasks.task1.model.Product;
import ru.ylab.tasks.task1.repository.jdbc.JdbcProductRepository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcProductRepositoryTest {

    private JdbcProductRepository repository;

    @BeforeEach
    void setUp() {
        TestDatabase.clearData();
        repository = new JdbcProductRepository(TestDatabase.getConnection());
    }

    @Test
    void save_shouldInsertNewProduct() {
        Product product = new Product("Test Product", "Electronics", "TestBrand",
                new BigDecimal("999.99"), "Test description");

        repository.save(product);

        assertThat(product.getId()).isNotNull();

        Optional<Product> found = repository.findById(product.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Product");
    }

    @Test
    void save_shouldUpdateExistingProduct() {
        Product product = new Product("Original Name", "Category", "Brand",
                new BigDecimal("100.00"), "Desc");
        repository.save(product);
        Long id = product.getId();

        product.setName("Updated Name");
        product.setPrice(new BigDecimal("200.00"));
        repository.save(product);

        Optional<Product> updated = repository.findById(id);
        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("Updated Name");
        assertThat(updated.get().getPrice()).isEqualTo(new BigDecimal("200.00"));
    }

    @Test
    void findAll_shouldReturnAllProducts() {
        repository.save(new Product("Product1", "Cat1", "Brand1", new BigDecimal("100"), "Desc1"));
        repository.save(new Product("Product2", "Cat2", "Brand2", new BigDecimal("200"), "Desc2"));

        Collection<Product> products = repository.findAll();

        assertThat(products).hasSize(2);
    }

    @Test
    void findById_shouldReturnProductWhenExists() {
        Product product = new Product("Test", "Cat", "Brand", new BigDecimal("50"), "Desc");
        repository.save(product);

        Optional<Product> found = repository.findById(product.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test");
    }

    @Test
    void findById_shouldReturnEmptyWhenNotExists() {
        Optional<Product> found = repository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    void deleteById_shouldRemoveProduct() {
        Product product = new Product("ToDelete", "Cat", "Brand", new BigDecimal("50"), "Desc");
        repository.save(product);
        Long id = product.getId();

        repository.deleteById(id);

        Optional<Product> found = repository.findById(id);
        assertThat(found).isEmpty();
    }

    @Test
    void findByCategory_shouldReturnProductsInCategory() {
        repository.save(new Product("P1", "Electronics", "Brand", new BigDecimal("100"), "Desc"));
        repository.save(new Product("P2", "Books", "Brand", new BigDecimal("50"), "Desc"));
        repository.save(new Product("P3", "Electronics", "Brand", new BigDecimal("200"), "Desc"));

        Collection<Product> electronics = repository.findByCategory("Electronics");

        assertThat(electronics).hasSize(2);
        assertThat(electronics).allMatch(p -> p.getCategory().equals("Electronics"));
    }

    @Test
    void findByBrand_shouldReturnProductsOfBrand() {
        repository.save(new Product("P1", "Cat", "Samsung", new BigDecimal("100"), "Desc"));
        repository.save(new Product("P2", "Cat", "Apple", new BigDecimal("200"), "Desc"));
        repository.save(new Product("P3", "Cat", "Samsung", new BigDecimal("150"), "Desc"));

        Collection<Product> samsungProducts = repository.findByBrand("Samsung");

        assertThat(samsungProducts).hasSize(2);
        assertThat(samsungProducts).allMatch(p -> p.getBrand().equals("Samsung"));
    }

    @Test
    void findByPriceRange_shouldReturnProductsInPriceRange() {
        repository.save(new Product("P1", "Cat", "Brand", new BigDecimal("100"), "Desc"));
        repository.save(new Product("P2", "Cat", "Brand", new BigDecimal("200"), "Desc"));
        repository.save(new Product("P3", "Cat", "Brand", new BigDecimal("300"), "Desc"));

        Collection<Product> inRange = repository.findByPriceRange(new BigDecimal("150"), new BigDecimal("250"));

        assertThat(inRange).hasSize(1);
        assertThat(inRange.iterator().next().getPrice()).isEqualTo(new BigDecimal("200.00"));
    }

    @Test
    void getMinPrice_shouldReturnLowestPrice() {
        repository.save(new Product("P1", "Cat", "Brand", new BigDecimal("50"), "Desc"));
        repository.save(new Product("P2", "Cat", "Brand", new BigDecimal("100"), "Desc"));
        repository.save(new Product("P3", "Cat", "Brand", new BigDecimal("25"), "Desc"));

        Optional<BigDecimal> minPrice = repository.getMinPrice();

        assertThat(minPrice).isPresent();
        assertThat(minPrice.get()).isEqualTo(new BigDecimal("25.00"));
    }

    @Test
    void getMaxPrice_shouldReturnHighestPrice() {
        repository.save(new Product("P1", "Cat", "Brand", new BigDecimal("50"), "Desc"));
        repository.save(new Product("P2", "Cat", "Brand", new BigDecimal("100"), "Desc"));
        repository.save(new Product("P3", "Cat", "Brand", new BigDecimal("75"), "Desc"));

        Optional<BigDecimal> maxPrice = repository.getMaxPrice();

        assertThat(maxPrice).isPresent();
        assertThat(maxPrice.get()).isEqualTo(new BigDecimal("100.00"));
    }
}
