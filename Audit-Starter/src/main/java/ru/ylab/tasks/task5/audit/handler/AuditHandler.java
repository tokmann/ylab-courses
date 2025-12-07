package ru.ylab.tasks.task5.audit.handler;

/**
 * Интерфейс для обработки событий аудита.
 * Определяет контракт для регистрации аудит-событий.
 */
public interface AuditHandler {

    /**
     * Обрабатывает событие аудита.
     * @param action описание действия, которое было выполнено
     * @param args аргументы метода, вызвавшего событие аудита
     */
    void handle(String action, Object[] args);
}

