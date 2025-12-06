package ru.ylab.tasks.task5.timing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.ylab.tasks.task5.timing.aspect.TimingAspect;

@Configuration
public class LoggingConfiguration {

    @Bean
    public TimingAspect timingAspect() {
        return new TimingAspect();
    }
}