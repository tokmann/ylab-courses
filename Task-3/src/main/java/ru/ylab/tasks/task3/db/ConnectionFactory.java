package ru.ylab.tasks.task3.db;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Фабрика для получения соединений с базой данных.
 * Использует пул соединений Apache Tomcat для эффективного управления соединениями.
 */
public class ConnectionFactory {

    private static final DataSource dataSource = TomcatDataSourceFactory.getDataSource();

    private ConnectionFactory() {}

    /**
     * Возвращает соединение с базой данных из пула соединений.
     * @return соединение с базой данных
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
