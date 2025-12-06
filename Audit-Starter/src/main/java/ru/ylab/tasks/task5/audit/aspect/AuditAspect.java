package ru.ylab.tasks.task5.audit.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import ru.ylab.tasks.task5.audit.handler.AuditHandler;
import ru.ylab.tasks.task5.audit.annotation.Auditable;

@Aspect
public class AuditAspect {

    private final AuditHandler auditHandler;

    public AuditAspect(AuditHandler auditHandler) {
        this.auditHandler = auditHandler;
    }

    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint pjp, Auditable auditable) throws Throwable {
        Object result = pjp.proceed();
        auditHandler.handle(auditable.action(), pjp.getArgs());
        return result;
    }
}
