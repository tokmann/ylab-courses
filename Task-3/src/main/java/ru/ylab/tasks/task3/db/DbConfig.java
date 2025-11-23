package ru.ylab.tasks.task3.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Класс для работы с конфигурацией базы данных.
 * Загружает настройки из файла application.properties и предоставляет к ним доступ.
 */
public class DbConfig {

    private final Properties props = new Properties();

    /**
     * Создает новый экземпляр конфигурации.
     * Загружает настройки из файла application.properties в classpath.
     */
    public DbConfig() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Error while loading config", e);
        }
    }

    public String getUrl() {
        return props.getProperty("db.url");
    }

    public String getUsername() {
        return props.getProperty("db.username");
    }

    public String getPassword() {
        return props.getProperty("db.password");
    }

    public String getLiquibaseChangelog() {
        return props.getProperty("liquibase.change-log");
    }

    public String getDefaultSchema() {
        return props.getProperty("liquibase.defaultSchemaName");
    }

    public String getLiquibaseSchema() {
        return props.getProperty("liquibase.liquibaseSchemaName");
    }

    public String getInitSchemas() {
        return props.getProperty("liquibase.init-schemas");
    }

}

