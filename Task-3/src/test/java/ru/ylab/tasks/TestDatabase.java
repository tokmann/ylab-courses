package ru.ylab.tasks;

import org.testcontainers.containers.PostgreSQLContainer;
import ru.ylab.tasks.task3.db.DbConfig;
import ru.ylab.tasks.task3.db.migration.LiquibaseRunner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

public abstract class TestDatabase {

    private static final PostgreSQLContainer<?> POSTGRES;
    private static Connection CONNECTION;

    static {
        POSTGRES = new PostgreSQLContainer<>("postgres:15")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
        POSTGRES.start();
        initializeDatabase();
    }

    private static void initializeDatabase() {
        try {
            System.out.println("INITIALIZING TEST DATABASE");
            System.out.println("JDBC URL: " + POSTGRES.getJdbcUrl());

            Properties props = new Properties();
            props.setProperty("db.url", POSTGRES.getJdbcUrl());
            props.setProperty("db.username", POSTGRES.getUsername());
            props.setProperty("db.password", POSTGRES.getPassword());
            props.setProperty("db.schema", "public");
            props.setProperty("liquibase.change-log", "db/changelog/db.changelog-master.xml");

            DbConfig dbConfig = new TestDbConfig(props);

            CONNECTION = DriverManager.getConnection(
                    POSTGRES.getJdbcUrl(),
                    POSTGRES.getUsername(),
                    POSTGRES.getPassword()
            );

            System.out.println("Connection created: " + (CONNECTION != null));

            LiquibaseRunner liquibaseRunner = new LiquibaseRunner(dbConfig);
            liquibaseRunner.updateDatabase();
            System.out.println("Migrations completed");

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize test database", e);
        }
    }

    public static Connection getConnection() {
        return CONNECTION;
    }

    public static void clearData() {
        try {
            Statement stmt = CONNECTION.createStatement();
            stmt.executeUpdate("DELETE FROM marketplace.products");
            stmt.executeUpdate("DELETE FROM marketplace.users");
            stmt.executeUpdate("ALTER SEQUENCE marketplace.user_seq RESTART WITH 1");
            stmt.executeUpdate("ALTER SEQUENCE marketplace.product_seq RESTART WITH 1");
            stmt.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to clear test data", e);
        }
    }

    static class TestDbConfig extends DbConfig {
        private final Properties props;

        public TestDbConfig(Properties props) {
            this.props = props;
        }

        @Override
        public String getUrl() {
            return props.getProperty("db.url");
        }

        @Override
        public String getUsername() {
            return props.getProperty("db.username");
        }

        @Override
        public String getPassword() {
            return props.getProperty("db.password");
        }

        @Override
        public String getLiquibaseChangelog() {
            return props.getProperty("liquibase.change-log");
        }

    }
}
