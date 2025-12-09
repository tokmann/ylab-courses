package ru.ylab.tasks.task4.service.audit;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Сервис аудита.
 * Отвечает за логирование действий системы (например, авторизация, изменения данных и т.д.)
 */
@Service
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
