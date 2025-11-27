package ru.ylab.tasks.task3.db;

import ru.ylab.tasks.task3.model.Product;
import ru.ylab.tasks.task3.model.User;
import ru.ylab.tasks.task3.repository.IProductRepository;
import ru.ylab.tasks.task3.repository.IUserRepository;
import ru.ylab.tasks.task3.repository.inmemory.InMemoryProductRepositoryImpl;
import ru.ylab.tasks.task3.repository.inmemory.InMemoryUserRepositoryImpl;
import ru.ylab.tasks.task3.repository.jdbc.JdbcProductRepositoryImpl;
import ru.ylab.tasks.task3.repository.jdbc.JdbcUserRepositoryImpl;
import ru.ylab.tasks.task3.service.persistence.ProductFileService;
import ru.ylab.tasks.task3.service.persistence.UserFileService;

import java.util.Collection;
import java.util.List;

public class RepositoryFactory {

    public static IProductRepository createProductRepository(String type, DbConfig config) {

        return switch (type) {
            case "jdbc" -> new JdbcProductRepositoryImpl();

            case "memory" -> {
                String file = config.getProductsFile();
                Collection<Product> initialProducts =
                        (file != null && !file.isBlank())
                                ? new ProductFileService(file).loadProducts()
                                : List.of();
                yield new InMemoryProductRepositoryImpl(initialProducts);
            }

            default -> throw new IllegalArgumentException("Unknown product repo type: " + type);
        };
    }

    public static IUserRepository createUserRepository(String type, DbConfig config) {

        return switch (type) {
            case "jdbc" -> new JdbcUserRepositoryImpl();

            case "memory" -> {
                String file = config.getUsersFile();
                Collection<User> initialUsers =
                        (file != null && !file.isBlank())
                                ? new UserFileService(file).loadUsers()
                                : List.of();
                yield new InMemoryUserRepositoryImpl(initialUsers);
            }

            default -> throw new IllegalArgumentException("Unknown user repo type: " + type);
        };
    }
}

