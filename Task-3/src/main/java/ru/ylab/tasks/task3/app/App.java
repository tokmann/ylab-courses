package ru.ylab.tasks.task3.app;

import ru.ylab.tasks.task3.controller.ProductController;
import ru.ylab.tasks.task3.controller.UserController;
import ru.ylab.tasks.task3.db.DbConfig;
import ru.ylab.tasks.task3.db.migration.LiquibaseRunner;
import ru.ylab.tasks.task3.repository.ProductRepository;
import ru.ylab.tasks.task3.repository.UserRepository;
import ru.ylab.tasks.task3.repository.jdbc.JdbcProductRepository;
import ru.ylab.tasks.task3.repository.jdbc.JdbcUserRepository;
import ru.ylab.tasks.task3.security.AuthService;
import ru.ylab.tasks.task3.service.AuditService;
import ru.ylab.tasks.task3.service.MetricService;
import ru.ylab.tasks.task3.service.ProductService;
import ru.ylab.tasks.task3.ui.ConsoleUI;

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
