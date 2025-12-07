package ru.ylab.tasks.task5.audit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.ylab.tasks.task5.audit.aspect.AuditAspect;
import ru.ylab.tasks.task5.audit.handler.AuditHandler;
import ru.ylab.tasks.task5.audit.handler.DefaultAuditHandler;

/**
 * Конфигурация Spring для автоматической настройки системы аудита.
 * Предоставляет бины по умолчанию для AuditAspect и AuditHandler,
 * если они не определены в пользовательской конфигурации.
 * Для использования системы аудита достаточно:
 * 1. Добавить зависимость на этот модуль
 * 2. Пометить методы аннотацией @Auditable
 * 3. При необходимости предоставить свою реализацию AuditHandler
 */
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
