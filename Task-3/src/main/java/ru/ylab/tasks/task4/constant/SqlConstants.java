package ru.ylab.tasks.task4.constant;

/**
 * Класс констант SQL запросов для работы с базой данных.
 * Содержит все SQL выражения, используемые в репозиториях приложения.
 */
public final class SqlConstants {

    private SqlConstants() {}

    // Query продуктов
    /**
     * SQl запрос для вставки нового продукта
     * */
    public static final String INSERT_PRODUCT =
            "INSERT INTO marketplace.products (name, category, brand, price, description) VALUES (?, ?, ?, ?, ?) RETURNING id";

    /**
     * SQl запрос для обновления существующего продукта
     * */
    public static final String UPDATE_PRODUCT =
            "UPDATE marketplace.products SET name=?, category=?, brand=?, price=?, description=? WHERE id=?";

    /**
     * SQl запрос для получения всех продуктов
     * */
    public static final String SELECT_ALL_PRODUCTS =
            "SELECT id, name, category, brand, price, description FROM marketplace.products";

    /**
     * SQl запрос для поиска продукта по id
     * */
    public static final String SELECT_PRODUCT_BY_ID =
            "SELECT id, name, category, brand, price, description FROM marketplace.products WHERE id=?";

    /**
     * SQl запрос для удаления продукта по id
     * */
    public static final String DELETE_PRODUCT_BY_ID =
            "DELETE FROM marketplace.products WHERE id=?";

    /**
     * SQl запрос для поиска продуктов по категории
     * */
    public static final String SELECT_PRODUCTS_BY_CATEGORY =
            "SELECT id, name, category, brand, price, description FROM marketplace.products WHERE category=?";

    /**
     * SQl запрос для поиска продуктов по бренду
     * */
    public static final String SELECT_PRODUCTS_BY_BRAND =
            "SELECT id, name, category, brand, price, description FROM marketplace.products WHERE brand=?";

    /**
     * SQl запрос для поиска продуктов по ценовому диапазону
     * */
    public static final String SELECT_PRODUCTS_BY_PRICE_RANGE =
            "SELECT id, name, category, brand, price, description FROM marketplace.products WHERE price BETWEEN ? AND ?";

    /**
     * SQl запрос для получения минимальной цены среди всех продуктов
     * */
    public static final String SELECT_MIN_PRICE =
            "SELECT MIN(price) AS min_price FROM marketplace.products";

    /**
     * SQl запрос для получения максимальной цены среди всех продуктов
     * */
    public static final String SELECT_MAX_PRICE =
            "SELECT MAX(price) AS max_price FROM marketplace.products";


    // Query пользователей
    /**
     * SQl запрос для добавления нового пользователя
     * */
    public static final String INSERT_USER =
            "INSERT INTO marketplace.users (login, password, role) VALUES (?, ?, ?) RETURNING id";

    /**
     * SQl запрос для обновления существующего пользователя
     * */
    public static final String UPDATE_USER =
            "UPDATE marketplace.users SET login = ?, password = ?, role = ? WHERE id = ?";

    /**
     * SQl запрос для получения всех пользователей
     * */
    public static final String SELECT_ALL_USERS =
            "SELECT id, login, password, role FROM marketplace.users ORDER BY id";

    /**
     * SQl запрос для поиска пользователя по id
     * */
    public static final String SELECT_USER_BY_ID =
            "SELECT id, login, password, role FROM marketplace.users WHERE id = ?";

    /**
     * SQl запрос для поиска пользователя по логину
     * */
    public static final String SELECT_USER_BY_LOGIN =
            "SELECT id, login, password, role FROM marketplace.users WHERE login = ?";

    /**
     * SQl запрос для проверки существования пользователя с указанным логином
     * */
    public static final String EXISTS_USER_BY_LOGIN =
            "SELECT 1 FROM marketplace.users WHERE login = ? LIMIT 1";

    /**
     * SQl запрос для удаления пользователя по id
     * */
    public static final String DELETE_USER_BY_ID =
            "DELETE FROM marketplace.users WHERE id = ?";
}
