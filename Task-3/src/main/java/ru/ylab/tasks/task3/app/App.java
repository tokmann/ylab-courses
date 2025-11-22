package ru.ylab.tasks.task3.app;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ru.ylab.tasks.task3.controller.ProductController;
import ru.ylab.tasks.task3.controller.UserController;
import ru.ylab.tasks.task3.db.DbConfig;
import ru.ylab.tasks.task3.db.migration.LiquibaseRunner;
import ru.ylab.tasks.task3.repository.ProductRepository;
import ru.ylab.tasks.task3.repository.UserRepository;
import ru.ylab.tasks.task3.repository.jdbc.JdbcProductRepository;
import ru.ylab.tasks.task3.repository.jdbc.JdbcUserRepository;
import ru.ylab.tasks.task3.security.AuthService;
import ru.ylab.tasks.task3.service.audit.AuditService;
import ru.ylab.tasks.task3.service.performance.MetricService;
import ru.ylab.tasks.task3.service.product.ProductService;
import ru.ylab.tasks.task3.ui.ConsoleUI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@WebListener
public class App implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
        AuditService audit = new AuditService();
        DbConfig dbConfig = new DbConfig();

        try (Connection connection = DriverManager.getConnection(
                dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword())) {

            // Прогоняем миграции Liquibase
            LiquibaseRunner liquibaseRunner = new LiquibaseRunner(dbConfig);
            liquibaseRunner.updateDatabase();

            // Создаём репозитории
            ProductRepository productRepository = new JdbcProductRepository(connection);
            UserRepository userRepository = new JdbcUserRepository(connection);

            // Сервисы
            ProductService productService = new ProductService(productRepository);
            AuthService authService = new AuthService(userRepository);
            MetricService metricService = new MetricService();

            // Контроллеры
            ProductController productController = new ProductController(productService, authService, audit);
            UserController userController = new UserController(authService, audit);

            // Сохраняем контроллеры в контекст
            sce.getServletContext().setAttribute("productController", productController);
            sce.getServletContext().setAttribute("userController", userController);


        } catch (SQLException e) {
            System.err.println("Ошибка подключения к базе: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ошибка приложения: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
