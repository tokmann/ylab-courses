package ru.ylab.tasks.task5.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.ylab.tasks.task5.repository.ProductRepository;
import ru.ylab.tasks.task5.repository.UserRepository;
import ru.ylab.tasks.task5.repository.jdbc.JdbcProductRepositoryImpl;
import ru.ylab.tasks.task5.repository.jdbc.JdbcUserRepositoryImpl;
import javax.sql.DataSource;

/**
 * Конфигурационный класс для создания репозиториев на основе типа, указанного в настройках.
 * Определяет фабричные методы для создания экземпляров ProductRepository и UserRepository.
 * Тип репозитория определяется параметром repository.type из конфигурации приложения.
 */
@Configuration
public class RepositoryConfiguration {

    /**
     * Создает экземпляр ProductRepository в зависимости от указанного типа.
     * Поддерживаемые типы:
     * - "jdbc": создает JdbcProductRepositoryImpl
     * @param dataSource источник данных для подключения к базе данных
     * @param type тип репозитория из конфигурации
     * @return экземпляр ProductRepository
     */
    @Bean
    public ProductRepository productRepository(
            DataSource dataSource,
            @Value("${repository.type}") String type
    ) {
        return switch (type) {
            case "jdbc" -> new JdbcProductRepositoryImpl(dataSource);
            default -> throw new IllegalArgumentException("Unknown product repo type");
        };
    }

    /**
     * Создает экземпляр UserRepository в зависимости от указанного типа.
     * Поддерживаемые типы:
     * - "jdbc": создает JdbcUserRepositoryImpl
     * @param dataSource источник данных для подключения к базе данных
     * @param type тип репозитория из конфигурации
     * @return экземпляр UserRepository
     */
    @Bean
    public UserRepository userRepository(
            DataSource dataSource,
            @Value("${repository.type}") String type
    ) {
        return switch (type) {
            case "jdbc" -> new JdbcUserRepositoryImpl(dataSource);
            default -> throw new IllegalArgumentException("Unknown user repo type");
        };
    }
}

