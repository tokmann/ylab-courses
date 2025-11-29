package ru.ylab.tasks.task4.config;

import liquibase.integration.spring.SpringLiquibase;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.util.Properties;

@Configuration
public class LiquibaseConfiguration {

    @Bean
    public SpringLiquibase initLiquibase(@Qualifier("yamlProperties") Properties props, DataSource ds) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(ds);
        liquibase.setChangeLog(props.getProperty("liquibase.init-schemas"));
        liquibase.setShouldRun(true);
        return liquibase;
    }

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
