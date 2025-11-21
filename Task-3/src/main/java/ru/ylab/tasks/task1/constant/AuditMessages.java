package ru.ylab.tasks.task1.constant;

import ru.ylab.tasks.task1.service.AuditService;

/**
 * Константы сообщений для аудита действий пользователей.
 * Используются вместе с {@link AuditService} для логирования операций
 * добавления, изменения, удаления товаров и действий с пользователями.
 */
public final class AuditMessages {
    private AuditMessages() {}

    /** Сообщения о действиях с товарами */
    public static final String PRODUCT_ADDED = "Пользователь '%s' добавил товар: %s";
    public static final String PRODUCT_UPDATED = "Пользователь '%s' обновил товар: %s";
    public static final String PRODUCT_DELETED = "Пользователь '%s' удалил товар: %s";
    public static final String PRODUCT_SEARCH = "Пользователь '%s' выполняет поиск товаров";

    /** Сообщения о действиях с пользователями */
    public static final String USER_REGISTERED = "Зарегистрирован новый пользователь: %s (role=%s)";
    public static final String USER_REGISTER_FAILED = "Неудачная попытка регистрации: %s";

    /** Сообщения о входе/выходе пользователей */
    public static final String LOGIN_SUCCESS = "Пользователь вошёл: %s";
    public static final String LOGIN_FAILED = "Неудачная попытка входа: %s";
    public static final String LOGOUT_SUCCESS = "Пользователь вышёл: %s";
}
