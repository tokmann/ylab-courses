package ru.ylab.tasks.task1.app;

import ru.ylab.tasks.task1.controller.ProductController;
import ru.ylab.tasks.task1.controller.UserController;
import ru.ylab.tasks.task1.db.DbConfig;
import ru.ylab.tasks.task1.db.migration.LiquibaseRunner;
import ru.ylab.tasks.task1.repository.ProductRepository;
import ru.ylab.tasks.task1.repository.UserRepository;
import ru.ylab.tasks.task1.repository.jdbc.JdbcProductRepository;
import ru.ylab.tasks.task1.repository.jdbc.JdbcUserRepository;
import ru.ylab.tasks.task1.security.AuthService;
import ru.ylab.tasks.task1.service.AuditService;
import ru.ylab.tasks.task1.service.MetricService;
import ru.ylab.tasks.task1.service.ProductService;
import ru.ylab.tasks.task1.ui.ConsoleUI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) {
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

            // Контроллеры
            ProductController productController = new ProductController(productService, authService, audit);
            UserController userController = new UserController(authService, audit);

            MetricService metricService = new MetricService();

            // UI
            ConsoleUI ui = new ConsoleUI(productController, userController, authService, metricService);

            // запуск приложения
            ui.start();

        } catch (SQLException e) {
            System.err.println("Ошибка подключения к базе: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ошибка приложения: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
