package ru.ylab.tasks.task4;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.ylab.tasks.task4.constant.Role;
import ru.ylab.tasks.task4.model.User;
import ru.ylab.tasks.task4.security.AuthService;
import ru.ylab.tasks.task4.repository.UserRepository;
import ru.ylab.tasks.task4.service.product.ProductService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractIntegrationTest {

    protected static final PostgreSQLContainer<?> postgreSQLContainer;

    static {
        postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine")
                .withDatabaseName("marketplace_test")
                .withUsername("test_user")
                .withPassword("test_pass");

        postgreSQLContainer.start();

        System.setProperty("db.url", postgreSQLContainer.getJdbcUrl());
        System.setProperty("db.username", postgreSQLContainer.getUsername());
        System.setProperty("db.password", postgreSQLContainer.getPassword());
        System.setProperty("repository.type", "jdbc");
        System.setProperty("repository.products-file", "");
        System.setProperty("repository.users-file", "");

    }

    @Autowired
    protected WebApplicationContext webApplicationContext;
    protected MockMvc mockMvc;
    protected AuthService authService;
    protected ProductService productService;
    protected UserRepository userRepository;
    protected DataSource dataSource;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();

        this.authService = this.webApplicationContext.getBean(AuthService.class);
        this.productService = this.webApplicationContext.getBean(ProductService.class);
        this.userRepository = this.webApplicationContext.getBean(UserRepository.class);
        this.dataSource = this.webApplicationContext.getBean(DataSource.class);

        cleanDatabase();
    }

    @AfterEach
    void tearDown() {
        logout();
    }

    protected void cleanDatabase() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("DELETE FROM marketplace.products");
            statement.execute("DELETE FROM marketplace.users");

            statement.execute("ALTER SEQUENCE marketplace.user_seq RESTART WITH 1");
            statement.execute("ALTER SEQUENCE marketplace.product_seq RESTART WITH 1");

        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean database", e);
        }
    }

    protected void loginAsAdmin() {
        if (!userRepository.existsByLogin("admin")) {
            User admin = new User();
            admin.setLogin("admin");
            admin.setPassword("admin123");
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }

        authService.login("admin", "admin123");
    }

    protected void logout() {
        if (authService != null) {
            authService.logout();
        }
    }

    protected void createTestUser(String login, String password, Role role) {
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        user.setRole(role);
        userRepository.save(user);
    }
}
