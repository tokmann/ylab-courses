package ru.ylab.tasks.task1.app;

import ru.ylab.tasks.task1.constant.FileConstants;
import ru.ylab.tasks.task1.controller.ProductController;
import ru.ylab.tasks.task1.controller.UserController;
import ru.ylab.tasks.task1.model.Product;
import ru.ylab.tasks.task1.model.User;
import ru.ylab.tasks.task1.repository.inmemory.InMemoryProductRepository;
import ru.ylab.tasks.task1.repository.inmemory.InMemoryUserRepository;
import ru.ylab.tasks.task1.repository.ProductRepository;
import ru.ylab.tasks.task1.repository.UserRepository;
import ru.ylab.tasks.task1.security.AuthService;
import ru.ylab.tasks.task1.service.AuditService;
import ru.ylab.tasks.task1.service.MetricService;
import ru.ylab.tasks.task1.service.ProductService;
import ru.ylab.tasks.task1.service.persistence.ProductFileService;
import ru.ylab.tasks.task1.service.persistence.UserFileService;
import ru.ylab.tasks.task1.ui.ConsoleUI;

import java.util.List;

public class App {
    public static void main(String[] args) {
        // Инициализация зависимостей
        AuditService audit = new AuditService();

        ProductFileService productFileService = new ProductFileService(FileConstants.PRODUCT_FILE);
        List<Product> loadedProducts = productFileService.loadProducts();
        ProductRepository productRepository = new InMemoryProductRepository(loadedProducts);

        UserFileService userFileService = new UserFileService(FileConstants.USER_FILE);
        List<User> loadedUsers = userFileService.loadUsers();
        UserRepository userRepository = new InMemoryUserRepository(loadedUsers);

        ProductService productService = new ProductService(productRepository);
        AuthService authService = new AuthService(userRepository);

        ProductController productController = new ProductController(productService, authService, audit);
        UserController userController = new UserController(authService, audit);

        MetricService metricService = new MetricService();

        // Передача зависимостей в UI
        ConsoleUI ui = new ConsoleUI(productController, userController,
                authService, productFileService,
                userFileService, productRepository,
                userRepository, metricService);

        // запуск интерфейса
        ui.start();
    }
}
