package ru.ylab.tasks.task1.service;

import java.time.LocalDateTime;

/**
 * Сервис аудита.
 * Отвечает за логирование действий системы (например, авторизация, изменения данных и т.д.)
 */
public class AuditService {

    public void log(String message) {
        System.out.println("[" + LocalDateTime.now() + "] " + message);
    }
}
