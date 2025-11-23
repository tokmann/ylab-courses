package ru.ylab.tasks.task3.app;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ru.ylab.tasks.task3.aop.AuditAspect;
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
import ru.ylab.tasks.task3.service.product.ProductService;

@WebListener
public class App implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        try {
            // Миграции Liquibase
            LiquibaseRunner liquibaseRunner = new LiquibaseRunner(new DbConfig());
            liquibaseRunner.updateDatabase();

            // Репозитории
            ProductRepository productRepository = new JdbcProductRepository();
            UserRepository userRepository = new JdbcUserRepository();

            // Сервисы
            ProductService productService = new ProductService(productRepository);
            AuthService authService = new AuthService(userRepository);
            AuditService auditService = new AuditService();

            // Зависимости аспекта для аудита
            AuditAspect.setAuditService(auditService);
            AuditAspect.setAuthService(authService);

            // Контроллеры
            ProductController productController = new ProductController(productService);
            UserController userController = new UserController(authService);

            // Сохранение контроллеров в контекст
            ctx.setAttribute("productController", productController);
            ctx.setAttribute("userController", userController);

            System.out.println("Приложение запущено успешно");

        } catch (Exception e) {
            System.err.println("Ошибка приложения: " + e.getMessage());
            e.printStackTrace();
        }
    }
}