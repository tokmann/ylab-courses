//package ru.ylab.tasks.task5;
//
//import org.apache.tomcat.jdbc.pool.DataSource;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import java.util.Properties;
//
//@Configuration
//@EnableWebMvc
//@ComponentScan(basePackages = "ru.ylab.tasks.task5")
//public class TestIntegrationConfiguration implements WebMvcConfigurer {
//
//    @Bean(name = "yamlProperties")
//    @Primary
//    public Properties yamlProperties() {
//        Properties props = new Properties();
//
//        String dbUrl = System.getProperty("db.url");
//        String dbUsername = System.getProperty("db.username");
//        String dbPassword = System.getProperty("db.password");
//
//        if (dbUrl == null) {
//
//            throw new IllegalStateException("System properties for database are not set. Make sure Testcontainers is properly configured.");
//        }
//
//        props.setProperty("db.url", System.getProperty("db.url"));
//        props.setProperty("db.username", dbUsername != null ? dbUsername : "test_user");
//        props.setProperty("db.password", dbPassword != null ? dbPassword : "test_pass");
//        props.setProperty("repository.type", System.getProperty("repository.type"));
//        props.setProperty("repository.products-file", System.getProperty("repository.products-file"));
//        props.setProperty("repository.users-file", System.getProperty("repository.users-file"));
//        props.setProperty("liquibase.init-schemas", "classpath:db/changelog/init-schemas.xml");
//        props.setProperty("liquibase.change-log", "classpath:db/changelog/db.changelog-master.xml");
//        props.setProperty("liquibase.default-schema", "marketplace");
//        props.setProperty("liquibase.liquibase-schema", "liquibase");
//
//        return props;
//    }
//
//    @Bean
//    @Primary
//    public DataSource dataSource(@Qualifier("yamlProperties") Properties yamlProperties) {
//        DataSource pool = new DataSource();
//        pool.setDriverClassName("org.postgresql.Driver");
//        pool.setUrl(yamlProperties.getProperty("db.url"));
//        pool.setUsername(yamlProperties.getProperty("db.username"));
//        pool.setPassword(yamlProperties.getProperty("db.password"));
//        pool.setMaxActive(20);
//        pool.setMaxIdle(10);
//        pool.setMaxWait(5000);
//        return pool;
//    }
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/swagger-ui/**")
//                .addResourceLocations("/swagger-ui/");
//        registry.addResourceHandler("/openapi.yaml")
//                .addResourceLocations("/");
//    }
//}