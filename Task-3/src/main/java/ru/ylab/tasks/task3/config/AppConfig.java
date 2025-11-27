package ru.ylab.tasks.task3.config;

import ru.ylab.tasks.task3.controller.ProductControllerImpl;
import ru.ylab.tasks.task3.controller.UserControllerImpl;
import ru.ylab.tasks.task3.controller.interfaces.IProductController;
import ru.ylab.tasks.task3.controller.interfaces.IUserController;
import ru.ylab.tasks.task3.db.DbConfig;
import ru.ylab.tasks.task3.db.RepositoryFactory;
import ru.ylab.tasks.task3.repository.IProductRepository;
import ru.ylab.tasks.task3.repository.IUserRepository;
import ru.ylab.tasks.task3.security.AuthServiceImpl;
import ru.ylab.tasks.task3.security.IAuthService;
import ru.ylab.tasks.task3.service.audit.AuditServiceImpl;
import ru.ylab.tasks.task3.service.audit.IAuditService;
import ru.ylab.tasks.task3.service.product.IProductService;
import ru.ylab.tasks.task3.service.product.ProductServiceImpl;

public class AppConfig {

    private final DbConfig dbConfig = new DbConfig();

    private IProductRepository productRepository;
    private IUserRepository userRepository;

    private IProductService productService;
    private IAuthService authService;
    private IAuditService auditService;

    private IProductController productController;
    private IUserController userController;

    public DbConfig dbConfig() {
        return dbConfig;
    }

    public IProductRepository productRepository() {
        if (productRepository == null) {
            productRepository = RepositoryFactory.createProductRepository(dbConfig.getRepositoryType(), dbConfig);
        }
        return productRepository;
    }

    public IUserRepository userRepository() {
        if (userRepository == null) {
            userRepository = RepositoryFactory.createUserRepository(dbConfig.getRepositoryType(), dbConfig);
        }
        return userRepository;
    }

    public IProductService productService() {
        if (productService == null) {
            productService = new ProductServiceImpl(productRepository());
        }
        return productService;
    }

    public IAuthService authService() {
        if (authService == null) {
            authService = new AuthServiceImpl(userRepository());
        }
        return authService;
    }

    public IAuditService auditService() {
        if (auditService == null) {
            auditService = new AuditServiceImpl();
        }
        return auditService;
    }

    public IProductController productController() {
        if (productController == null) {
            productController = new ProductControllerImpl(productService());
        }
        return productController;
    }

    public IUserController userController() {
        if (userController == null) {
            userController = new UserControllerImpl(authService());
        }
        return userController;
    }
}

