package ru.ylab.tasks.task5.audit.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import ru.ylab.tasks.task5.audit.handler.AuditHandler;
import ru.ylab.tasks.task5.audit.annotation.Auditable;

/**
 * Аспект Spring AOP для обработки аннотации Auditable.
 * Перехватывает выполнение методов, помеченных аннотацией @Auditable,
 * и делегирует обработку аудита компоненту AuditHandler.
 */
@Aspect
public class AuditAspect {

    private final AuditHandler auditHandler;

    public AuditAspect(AuditHandler auditHandler) {
        this.auditHandler = auditHandler;
    }

    /**
     * Совет, выполняемый вокруг методов, помеченных аннотацией @Auditable.
     * Метод выполняется после успешного выполнения метода и регистрирует
     * событие аудита через AuditHandler.
     * @param pjp точка соединения для доступа к информации о методе
     * @param auditable экземпляр аннотации с параметрами
     * @return результат выполнения целевого метода
     */
    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint pjp, Auditable auditable) throws Throwable {
        Object result = pjp.proceed();
        auditHandler.handle(auditable.action(), pjp.getArgs());
        return result;
    }
}
