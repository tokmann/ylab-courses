package ru.ylab.tasks.task1.constant;

public final class SqlConstants {

    private SqlConstants() {}

    // Query продуктов
    public static final String INSERT_PRODUCT =
            "INSERT INTO marketplace.products (name, category, brand, price, description) VALUES (?, ?, ?, ?, ?) RETURNING id";
    public static final String UPDATE_PRODUCT =
            "UPDATE marketplace.products SET name=?, category=?, brand=?, price=?, description=? WHERE id=?";
    public static final String SELECT_ALL_PRODUCTS =
            "SELECT id, name, category, brand, price, description FROM marketplace.products";
    public static final String SELECT_PRODUCT_BY_ID =
            "SELECT id, name, category, brand, price, description FROM marketplace.products WHERE id=?";
    public static final String DELETE_PRODUCT_BY_ID =
            "DELETE FROM marketplace.products WHERE id=?";
    public static final String SELECT_PRODUCTS_BY_CATEGORY =
            "SELECT id, name, category, brand, price, description FROM marketplace.products WHERE category=?";
    public static final String SELECT_PRODUCTS_BY_BRAND =
            "SELECT id, name, category, brand, price, description FROM marketplace.products WHERE brand=?";
    public static final String SELECT_PRODUCTS_BY_PRICE_RANGE =
            "SELECT id, name, category, brand, price, description FROM marketplace.products WHERE price BETWEEN ? AND ?";
    public static final String SELECT_MIN_PRICE =
            "SELECT MIN(price) AS min_price FROM marketplace.products";
    public static final String SELECT_MAX_PRICE =
            "SELECT MAX(price) AS max_price FROM marketplace.products";

    // Query пользователей
    public static final String INSERT_USER =
            "INSERT INTO marketplace.users (login, password, role) VALUES (?, ?, ?) RETURNING id";
    public static final String UPDATE_USER =
            "UPDATE marketplace.users SET login = ?, password = ?, role = ? WHERE id = ?";
    public static final String SELECT_ALL_USERS =
            "SELECT id, login, password, role FROM marketplace.users ORDER BY id";
    public static final String SELECT_USER_BY_ID =
            "SELECT id, login, password, role FROM marketplace.users WHERE id = ?";
    public static final String SELECT_USER_BY_LOGIN =
            "SELECT id, login, password, role FROM marketplace.users WHERE login = ?";
    public static final String EXISTS_USER_BY_LOGIN =
            "SELECT 1 FROM marketplace.users WHERE login = ? LIMIT 1";
    public static final String DELETE_USER_BY_ID =
            "DELETE FROM marketplace.users WHERE id = ?";
}
