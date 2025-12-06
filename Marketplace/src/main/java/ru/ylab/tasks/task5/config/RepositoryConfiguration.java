package ru.ylab.tasks.task5.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.ylab.tasks.task5.repository.ProductRepository;
import ru.ylab.tasks.task5.repository.UserRepository;
import ru.ylab.tasks.task5.repository.jdbc.JdbcProductRepositoryImpl;
import ru.ylab.tasks.task5.repository.jdbc.JdbcUserRepositoryImpl;
import javax.sql.DataSource;

@Configuration
public class RepositoryConfiguration {

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

