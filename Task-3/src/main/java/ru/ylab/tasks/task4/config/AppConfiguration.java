package ru.ylab.tasks.task4.config;


import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.ylab.tasks.task4.model.Product;
import ru.ylab.tasks.task4.model.User;
import ru.ylab.tasks.task4.repository.IProductRepository;
import ru.ylab.tasks.task4.repository.IUserRepository;
import ru.ylab.tasks.task4.repository.inmemory.InMemoryProductRepositoryImpl;
import ru.ylab.tasks.task4.repository.inmemory.InMemoryUserRepositoryImpl;
import ru.ylab.tasks.task4.repository.jdbc.JdbcProductRepositoryImpl;
import ru.ylab.tasks.task4.repository.jdbc.JdbcUserRepositoryImpl;
import ru.ylab.tasks.task4.security.AuthServiceImpl;
import ru.ylab.tasks.task4.security.IAuthService;
import ru.ylab.tasks.task4.service.audit.AuditServiceImpl;
import ru.ylab.tasks.task4.service.audit.IAuditService;
import ru.ylab.tasks.task4.service.persistence.ProductFileService;
import ru.ylab.tasks.task4.service.persistence.UserFileService;
import ru.ylab.tasks.task4.service.product.IProductService;
import ru.ylab.tasks.task4.service.product.ProductServiceImpl;

import java.util.*;

/**
 * Основной конфигурационный класс Spring приложения.
 * Настраивает компоненты, бины и web-инфраструктуру приложения.
 */
@Configuration
@EnableAspectJAutoProxy
@EnableWebMvc
@ComponentScan(basePackages = "ru.ylab.tasks.task4")
public class AppConfiguration implements WebMvcConfigurer {

    /**
     * Создает бин свойств приложения из YAML файла.
     * @return Properties с настройками приложения
     */
    @Bean("yamlProperties")
    public Properties yamlProperties() {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(new ClassPathResource("application.yml"));
        return factory.getObject();
    }

    /**
     * Создает бин источника данных для подключения к базе данных.
     * Настраивает пул соединений с параметрами из свойств приложения.
     * @param yamlProperties свойства приложения
     * @return настроенный DataSource
     */
    @Bean
    public DataSource dataSource(@Qualifier("yamlProperties") Properties yamlProperties) {
        DataSource pool = new DataSource();
        pool.setDriverClassName("org.postgresql.Driver");
        pool.setUrl(yamlProperties.getProperty("db.url"));
        pool.setUsername(yamlProperties.getProperty("db.username"));
        pool.setPassword(yamlProperties.getProperty("db.password"));
        pool.setMaxActive(100);
        pool.setMaxIdle(30);
        pool.setMaxWait(10000);
        return pool;
    }

    /**
     * Создает бин репозитория продуктов в зависимости от конфигурации.
     * Поддерживает JDBC и in-memory реализации репозитория.
     * @param yamlProperties свойства приложения
     * @param dataSource источник данных
     * @return реализация IProductRepository
     */
    @Bean
    public IProductRepository productRepository(@Qualifier("yamlProperties") Properties yamlProperties, DataSource dataSource) {
        String type = yamlProperties.getProperty("repository.type");
        String file = yamlProperties.getProperty("repository.products-file");

        return switch (type) {
            case "jdbc" -> new JdbcProductRepositoryImpl(dataSource);
            case "memory" -> {
                Collection<Product> initial = (!file.isBlank())
                        ? new ProductFileService(file).loadProducts()
                        : List.of();
                yield new InMemoryProductRepositoryImpl(initial);
            }
            default -> throw new IllegalArgumentException("Unknown product repo type");
        };
    }

    /**
     * Создает бин репозитория пользователей в зависимости от конфигурации.
     * Поддерживает JDBC и in-memory реализации репозитория.
     * @param yamlProperties свойства приложения
     * @param dataSource источник данных
     * @return реализация IUserRepository
     */
    @Bean
    public IUserRepository userRepository(@Qualifier("yamlProperties") Properties yamlProperties, DataSource dataSource) {
        String type = yamlProperties.getProperty("repository.type");
        String file = yamlProperties.getProperty("repository.users-file");

        return switch (type) {
            case "jdbc" -> new JdbcUserRepositoryImpl(dataSource);
            case "memory" -> {
                Collection<User> initial = (!file.isBlank())
                        ? new UserFileService(file).loadUsers()
                        : List.of();
                yield new InMemoryUserRepositoryImpl(initial);
            }
            default -> throw new IllegalArgumentException("Unknown user repo type");
        };
    }

    /**
     * Настраивает обработчики статических ресурсов для веб-интерфейса.
     * Регистрирует пути для Swagger UI и OpenAPI спецификации.
     * @param registry реестр обработчиков ресурсов
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("/swagger-ui/");

        registry.addResourceHandler("/openapi.yaml")
                .addResourceLocations("/");
    }
}

