package ru.ylab.tasks.task3.db;

import org.apache.tomcat.jdbc.pool.DataSource;

public class TomcatDataSourceFactory {

    private static DataSource dataSource;

    static {
        try {
            DataSource pool = new DataSource();

            DbConfig dbConfig = new DbConfig();

            pool.setDriverClassName("org.postgresql.Driver");
            pool.setUrl(dbConfig.getUrl());
            pool.setUsername(dbConfig.getUsername());
            pool.setPassword(dbConfig.getPassword());

            pool.setMaxActive(100);
            pool.setMaxIdle(30);
            pool.setMaxWait(10000);

            dataSource = pool;

        } catch (Exception e) {
            throw new RuntimeException("Error creating DataSource", e);
        }
    }

    private TomcatDataSourceFactory() {}

    public static DataSource getDataSource() {
        return dataSource;
    }

}
