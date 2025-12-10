package ru.ylab.tasks.task5;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.ylab.tasks.task5.constant.Role;
import ru.ylab.tasks.task5.model.User;
import ru.ylab.tasks.task5.repository.UserRepository;
import ru.ylab.tasks.task5.security.AuthService;
import ru.ylab.tasks.task5.service.product.ProductService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static ru.ylab.tasks.task5.LiquibaseTestUtil.runMigrations;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

    protected static final PostgreSQLContainer<?> postgreSQLContainer;

    static {
        postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine")
                .withDatabaseName("marketplace_test")
                .withUsername("test_user")
                .withPassword("test_pass");

        postgreSQLContainer.start();

        System.setProperty("spring.datasource.url", postgreSQLContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgreSQLContainer.getUsername());
        System.setProperty("spring.datasource.password", postgreSQLContainer.getPassword());
        System.setProperty("repository.type", "jdbc");
        System.setProperty("repository.products-file", "");
        System.setProperty("repository.users-file", "");
    }

    @Autowired
    protected WebApplicationContext webApplicationContext;
    protected MockMvc mockMvc;

    @Autowired
    protected AuthService authService;

    @Autowired
    protected ProductService productService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected DataSource dataSource;

    protected ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setUpSchemasAndTables() throws Exception {
        runMigrations(postgreSQLContainer);
    }

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
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