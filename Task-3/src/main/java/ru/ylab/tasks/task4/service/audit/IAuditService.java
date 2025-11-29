package ru.ylab.tasks.task4.service.audit;

/**
 * Интерфейс сервиса аудита действий пользователей.
 * Используется для логирования операций.
 */
public interface IAuditService {

    /**
     * Логирует сообщение с временной меткой.
     */
    void log(String message);
}

