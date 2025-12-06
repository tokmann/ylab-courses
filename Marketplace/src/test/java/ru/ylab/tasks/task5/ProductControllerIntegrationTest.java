package ru.ylab.tasks.task5;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import ru.ylab.tasks.task5.dto.request.product.ProductCreateRequest;
import ru.ylab.tasks.task5.dto.request.product.ProductDeleteRequest;
import ru.ylab.tasks.task5.dto.request.product.ProductSearchRequest;
import ru.ylab.tasks.task5.model.Product;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerIntegrationTest extends AbstractIntegrationTest {

    @BeforeEach
    void setUp() {
        super.setUp();
        loginAsAdmin();
    }

    @Test
    @DisplayName("Создание продукта: должен вернуть статус Created при корректном запросе")
    void createProduct_ShouldReturnCreated_WhenValidRequest() throws Exception {
        ProductCreateRequest request = new ProductCreateRequest();
        request.setName("Test Product");
        request.setCategory("Electronics");
        request.setBrand("Test Brand");
        request.setPrice("999.99");
        request.setDescription("Test Description");

        SoftAssertions softly = new SoftAssertions();

        mockMvc.perform(post("/marketplace/products/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").exists())
                .andExpect(jsonPath("$.message").value("Product created successfully"));

        List<Product> products = productService.getAll();

        softly.assertThat(products).hasSize(1);
        softly.assertThat(products.get(0).getName()).isEqualTo("Test Product");

        softly.assertAll();
    }

    @Test
    @DisplayName("Создание продукта: должен вернуть Unauthorized когда пользователь не авторизован")
    void createProduct_ShouldReturnUnauthorized_WhenNotLoggedIn() throws Exception {
        logout();
        ProductCreateRequest request = new ProductCreateRequest();
        request.setName("Test Product");

        mockMvc.perform(post("/marketplace/products/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Получение всех продуктов: должен вернуть список продуктов для авторизованного пользователя")
    void getAllProducts_ShouldReturnProducts_WhenAuthenticated() throws Exception {
        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setCategory("Category 1");
        product1.setBrand("Brand 1");
        product1.setPrice(new BigDecimal("100.00"));
        productService.create(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setCategory("Category 2");
        product2.setBrand("Brand 2");
        product2.setPrice(new BigDecimal("200.00"));
        productService.create(product2);

        SoftAssertions softly = new SoftAssertions();

        mockMvc.perform(get("/marketplace/products/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products").isArray())
                .andExpect(jsonPath("$.products.length()").value(2))
                .andExpect(jsonPath("$.products[0].name").value("Product 1"))
                .andExpect(jsonPath("$.products[1].name").value("Product 2"));

        List<Product> products = productService.getAll();
        softly.assertThat(products).hasSize(2);

        softly.assertAll();
    }

    @Test
    @DisplayName("Удаление продукта: должен удалить продукт когда удаление выполняет администратор")
    void deleteProduct_ShouldDeleteProduct_WhenAdmin() throws Exception {
        Product product = new Product();
        product.setName("To Delete");
        product.setCategory("Test");
        product.setBrand("Test Brand");
        product.setPrice(new BigDecimal("50.00"));
        productService.create(product);
        Long productId = product.getId();

        ProductDeleteRequest request = new ProductDeleteRequest();
        request.setId(productId);

        SoftAssertions softly = new SoftAssertions();

        mockMvc.perform(post("/marketplace/products/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productId));

        List<Product> products = productService.getAll();
        boolean productExists = products.stream()
                .anyMatch(p -> p.getId().equals(productId));

        softly.assertThat(productExists).as("Продукт должен быть удален").isFalse();

        softly.assertAll();
    }

    @Test
    @DisplayName("Поиск продуктов: должен возвращать отфильтрованные результаты по категории")
    void searchProducts_ShouldReturnFilteredResults() throws Exception {
        Product laptop = new Product();
        laptop.setName("Gaming Laptop");
        laptop.setCategory("Electronics");
        laptop.setBrand("Apple");
        laptop.setPrice(new BigDecimal("1500.00"));
        productService.create(laptop);

        Product phone = new Product();
        phone.setName("Smartphone");
        phone.setCategory("Electronics");
        phone.setBrand("Samsung");
        phone.setPrice(new BigDecimal("800.00"));
        productService.create(phone);

        Product book = new Product();
        book.setName("Java Microservices");
        book.setCategory("Books");
        book.setBrand("O'Reilly");
        book.setPrice(new BigDecimal("50.00"));
        productService.create(book);

        ProductSearchRequest request = new ProductSearchRequest();
        request.setCategory("Electronics");

        SoftAssertions softly = new SoftAssertions();

        mockMvc.perform(post("/marketplace/products/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results.length()").value(2))
                .andExpect(jsonPath("$.results[?(@.name == 'Gaming Laptop')]").exists())
                .andExpect(jsonPath("$.results[?(@.name == 'Smartphone')]").exists());

        List<Product> allProducts = productService.getAll();
        softly.assertThat(allProducts).hasSize(3);

        softly.assertAll();
    }
}