package ru.ylab.tasks.task3.constant;

import ru.ylab.tasks.task3.ui.ConsoleUI;

/**
 * Константы для консольного интерфейса {@link ConsoleUI}.
 * Включают коды выбора пунктов меню для авторизации и основной работы с приложением.
 */
public final class ConsoleUIConstants {

    private ConsoleUIConstants() {}

    // Константы меню доступа
    public static final String MENU_LOGIN = "1";
    public static final String MENU_REGISTER = "2";
    public static final String MENU_EXIT = "0";

    // Константы основного меню
    public static final String MENU_ADD_PRODUCT = "1";
    public static final String MENU_UPDATE_PRODUCT = "2";
    public static final String MENU_DELETE_PRODUCT = "3";
    public static final String MENU_SHOW_ALL = "4";
    public static final String MENU_SEARCH = "5";
    public static final String MENU_LOGOUT = "6";
    public static final String MENU_EXIT_APP = "0";

}
