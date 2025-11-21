package ru.ylab.tasks.task3.db.migration;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import ru.ylab.tasks.task3.db.DbConfig;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Класс для выполнения миграций базы данных с помощью Liquibase.
 * Автоматически применяет изменения из changelog файлов к базе данных.
 */
public class LiquibaseRunner {

    private final DbConfig config;

    public LiquibaseRunner(DbConfig config) {
        this.config = config;
    }

    /**
     * Выполняет обновление базы данных до последней версии.
     * Применяет все changesets из указанного changelog файла.
     */
    public void updateDatabase() {
        try (Connection connection = DriverManager.getConnection(
                config.getUrl(),
                config.getUsername(),
                config.getPassword()
        )) {

            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibaseInit = new Liquibase(
                    config.getInitSchemas(),
                    new ClassLoaderResourceAccessor(),
                    database
            );
            liquibaseInit.update(new Contexts(), new LabelExpression());

            database.setDefaultSchemaName(config.getDefaultSchema());
            database.setLiquibaseSchemaName(config.getLiquibaseSchema());


            Liquibase liquibaseMain = new Liquibase(
                    config.getLiquibaseChangelog(),
                    new ClassLoaderResourceAccessor(),
                    database
            );
            liquibaseMain.update(new Contexts(), new LabelExpression());

        } catch (Exception e) {
            throw new RuntimeException("Ошибка Liquibase: " + e.getMessage(), e);
        }
    }
}
