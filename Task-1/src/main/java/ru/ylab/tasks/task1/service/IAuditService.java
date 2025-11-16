package ru.ylab.tasks.task1.service;

import ru.ylab.tasks.task1.model.Product;
import ru.ylab.tasks.task1.util.SearchFilter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

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

