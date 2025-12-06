package ru.ylab.tasks.task5;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.Statement;

import static ru.ylab.tasks.task5.constant.SqlConstants.CREATE_LIQUIBASE_SCHEMA;
import static ru.ylab.tasks.task5.constant.SqlConstants.CREATE_MARKETPLACE_SCHEMA;

public class LiquibaseTestUtil {

    public static void runMigrations(PostgreSQLContainer<?> container) throws Exception {
        String url = container.getJdbcUrl();
        String username = container.getUsername();
        String password = container.getPassword();

        try (Connection connection = java.sql.DriverManager.getConnection(url, username, password)) {

            Statement stmt = connection.createStatement();
            stmt.execute(CREATE_LIQUIBASE_SCHEMA);
            stmt.execute(CREATE_MARKETPLACE_SCHEMA);

            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase(
                    "db/changelog/db.changelog-master.xml",
                    new ClassLoaderResourceAccessor(),
                    database
            );

            liquibase.update(new Contexts(), new LabelExpression());
        }
    }
}
