package ru.ylab.tasks.task5.timing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.ylab.tasks.task5.timing.aspect.TimingAspect;

/**
 * Конфигурация Spring для настройки аспекта логирования времени выполнения.
 * Создает и регистрирует бин TimingAspect в контексте Spring.
 */
@Configuration
public class LoggingConfiguration {

    @Bean
    public TimingAspect timingAspect() {
        return new TimingAspect();
    }
}