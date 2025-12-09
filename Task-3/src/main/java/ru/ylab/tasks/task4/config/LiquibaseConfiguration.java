package ru.ylab.tasks.task4.config;

import liquibase.integration.spring.SpringLiquibase;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Конфигурационный класс для настройки Liquibase - инструмента миграции базы данных.
 * Определяет бины для инициализации схем и применения основных миграций.
 */
@Configuration
public class LiquibaseConfiguration {

    /**
     * Создает бин для инициализации схем базы данных.
     * Выполняет создание необходимых схем (liquibase, marketplace).
     * @param props свойства приложения
     * @param ds источник данных
     * @return настроенный экземпляр SpringLiquibase для инициализации схем
     */
    @Bean
    public SpringLiquibase initLiquibase(@Qualifier("yamlProperties") Properties props, DataSource ds) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(ds);
        liquibase.setChangeLog(props.getProperty("liquibase.init-schemas"));
        liquibase.setShouldRun(true);
        return liquibase;
    }
    /**
     * Создает бин для применения основных миграций базы данных.
     * Выполняет создание таблиц, последовательностей и заполнение начальными данными.
     * @param props свойства приложения
     * @param ds источник данных
     * @return настроенный экземпляр SpringLiquibase для основных миграций
     */
    @Bean
    public SpringLiquibase mainLiquibase(@Qualifier("yamlProperties") Properties props, DataSource ds) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(ds);
        liquibase.setChangeLog(props.getProperty("liquibase.change-log"));
        liquibase.setDefaultSchema(props.getProperty("liquibase.default-schema"));
        liquibase.setLiquibaseSchema(props.getProperty("liquibase.liquibase-schema"));
        liquibase.setShouldRun(true);
        return liquibase;
    }
}
