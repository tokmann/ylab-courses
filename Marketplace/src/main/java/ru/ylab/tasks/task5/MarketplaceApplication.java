package ru.ylab.tasks.task5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.ylab.tasks.task5.timing.annotation.EnableLogging;

/**
 * Класс инициализации веб-приложения Spring Boot.
 */
@SpringBootApplication
@EnableLogging
public class MarketplaceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MarketplaceApplication.class, args);
    }
}

