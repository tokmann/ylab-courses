package ru.ylab.tasks.task5.audit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.ylab.tasks.task5.audit.aspect.AuditAspect;
import ru.ylab.tasks.task5.audit.handler.AuditHandler;
import ru.ylab.tasks.task5.audit.handler.DefaultAuditHandler;

@Configuration
public class AuditAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AuditAspect auditAspect(AuditHandler handler) {
        return new AuditAspect(handler);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuditHandler auditHandler() {
        return new DefaultAuditHandler();
    }
}
