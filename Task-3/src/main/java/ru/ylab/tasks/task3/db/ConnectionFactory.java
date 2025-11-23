package ru.ylab.tasks.task3.db;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final DataSource dataSource = TomcatDataSourceFactory.getDataSource();

    private ConnectionFactory() {}

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
