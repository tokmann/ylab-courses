package ru.ylab.tasks.task5.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static ru.ylab.tasks.task5.constant.SqlConstants.CREATE_LIQUIBASE_SCHEMA;
import static ru.ylab.tasks.task5.constant.SqlConstants.CREATE_MARKETPLACE_SCHEMA;

/**
 * Конфигурационный класс для создания схем базы данных при старте приложения.
 * Выполняет создание необходимых схем в базе данных после инициализации бина.
 */
@Configuration
public class SchemaConfiguration {

    private final DataSource dataSource;

    public SchemaConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Метод, выполняемый после создания бина.
     * Создает схемы liquibase и marketplace в базе данных.
     */
    @PostConstruct
    public void createSchemas() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
             stmt.execute(CREATE_LIQUIBASE_SCHEMA);
             stmt.execute(CREATE_MARKETPLACE_SCHEMA);
        }
    }
}

