package ru.ylab.tasks.task3.service.audit;

import java.time.LocalDateTime;

/**
 * Сервис аудита.
 * Отвечает за логирование действий системы (например, авторизация, изменения данных и т.д.)
 */
public class AuditServiceImpl implements IAuditService{

    /**
     * Логирует сообщение с отметкой времени в консоль.
     * @param message сообщение для записи в лог
     */
    @Override
    public void log(String message) {
        System.out.println("[" + LocalDateTime.now() + "] " + message);
    }
}
