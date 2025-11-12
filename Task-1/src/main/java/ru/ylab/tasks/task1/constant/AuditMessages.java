package ru.ylab.tasks.task1.constant;

public final class AuditMessages {
    private AuditMessages() {}

    public static final String PRODUCT_ADDED = "Пользователь '%s' добавил товар: %s";
    public static final String PRODUCT_UPDATED = "Пользователь '%s' обновил товар: %s";
    public static final String PRODUCT_DELETED = "Пользователь '%s' удалил товар: %s";
    public static final String PRODUCT_SEARCH = "Пользователь '%s' выполняет поиск товаров";

    public static final String USER_REGISTERED = "Зарегистрирован новый пользователь: %s (role=%s)";
    public static final String USER_REGISTER_FAILED = "Неудачная попытка регистрации: %s";

    public static final String LOGIN_SUCCESS = "Пользователь вошёл: %s";
    public static final String LOGIN_FAILED = "Неудачная попытка входа: %s";
    public static final String LOGOUT_SUCCESS = "Пользователь вышёл: %s";
}
