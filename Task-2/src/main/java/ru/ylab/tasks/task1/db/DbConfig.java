package ru.ylab.tasks.task1.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DbConfig {

    private final Properties props = new Properties();

    public DbConfig() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить конфиг", e);
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

}

