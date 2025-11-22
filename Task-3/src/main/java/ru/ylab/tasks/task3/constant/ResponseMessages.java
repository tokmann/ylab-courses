package ru.ylab.tasks.task3.constant;

public final class ResponseMessages {

    private ResponseMessages() {}

    // Product
    public static final String PRODUCT_DELETED_SUCCESS = "Product deleted successfully";
    public static final String PRODUCT_CREATE_SUCCESS = "Product created successfully";
    public static final String PRODUCT_UPDATED_SUCCESS = "Product listed successfully";

    public static final String PRODUCT_ID_MISSING = "Product id must be provided";
    public static final String PRODUCT_ID_INVALID = "Product id must be a valid number";
    public static final String PRODUCT_INVALID_MIN_MAX_PRICE = "minPrice must be less than or equal to maxPrice";


    public static final String PRODUCT_DELETE_FAILED = "Failed to delete product";
    public static final String PRODUCT_CREATE_FAILED = "Failed to create product";
    public static final String PRODUCT_SEARCH_FAILED = "Failed to search products";

    // User
    public static final String USER_LOGIN_SUCCESS = "User logged in successfully";
    public static final String USER_LOGOUT_SUCCESS = "User logged out successfully";

    public static final String USER_FORBIDDEN = "Action is forbidden";
    public static final String USER_UNAUTHORIZED = "You are not authorized";

    // JSON
    public static final String INVALID_JSON = "Invalid JSON format";
    public static final String INVALID_DATA = "Invalid data format";
    public static final String VALIDATION_FAILED = "Validation failed";
}
