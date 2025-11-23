package ru.ylab.tasks.task3.constant;

import ru.ylab.tasks.task3.service.audit.AuditService;

/**
 * Константы сообщений для аудита действий пользователей.
 * Используются вместе с {@link AuditService} для логирования операций
 * добавления, изменения, удаления товаров и действий с пользователями.
 */
public final class AuditMessages {
    private AuditMessages() {}

    /** Сообщения о действиях с товарами */
    public static final String PRODUCT_ADDED = "User '%s' added product: %s";
    public static final String PRODUCT_UPDATED = "User '%s' updated product: %s";
    public static final String PRODUCT_DELETED = "User '%s' deleted product: %s";
    public static final String PRODUCT_SEARCH = "User '%s' is searching products";

    /** Сообщения о действиях с пользователями */
    public static final String USER_REGISTERED = "New user registered: %s (role=%s)";
    public static final String USER_REGISTER_FAILED = "Failed registration attempt: %s";

    /** Сообщения о входе/выходе пользователей */
    public static final String LOGIN_SUCCESS = "User logged in: %s";
    public static final String LOGIN_FAILED = "Failed login attempt: %s";
    public static final String LOGOUT_SUCCESS = "User logged out: %s";
}
