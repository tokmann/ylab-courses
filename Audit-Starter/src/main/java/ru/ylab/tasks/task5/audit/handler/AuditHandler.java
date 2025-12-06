package ru.ylab.tasks.task5.audit.handler;

public interface AuditHandler {
    void handle(String action, Object[] args);
}

