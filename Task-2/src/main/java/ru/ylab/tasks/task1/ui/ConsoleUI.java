package ru.ylab.tasks.task1.ui;

import ru.ylab.tasks.task1.controller.ProductController;
import ru.ylab.tasks.task1.controller.UserController;
import ru.ylab.tasks.task1.model.Product;
import ru.ylab.tasks.task1.model.User;
import ru.ylab.tasks.task1.repository.ProductRepository;
import ru.ylab.tasks.task1.repository.UserRepository;
import ru.ylab.tasks.task1.security.AuthService;
import ru.ylab.tasks.task1.service.MetricService;
import ru.ylab.tasks.task1.util.SearchFilter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

import static ru.ylab.tasks.task1.util.ConsoleUtils.*;
import static ru.ylab.tasks.task1.constant.ConsoleUIConstants.*;

/**
 * Консольный интерфейс для взаимодействия пользователя с системой управления товарами.
 * Реализует аутентификацию, регистрацию, операции CRUD над товарами и поиск.
 */
public class ConsoleUI {

    private final ProductController productController;
    private final UserController userController;
    private final AuthService authService;
    private final MetricService metricService;

    private final Scanner scanner = new Scanner(System.in);

    public ConsoleUI(ProductController productController,
                     UserController userController,
                     AuthService authService,
                     MetricService metricService) {
        this.productController = productController;
        this.userController = userController;
        this.authService = authService;
        this.metricService = metricService;
    }

    /**
     * Главный цикл работы приложения
     * В зависимости от статуса авторизации пользователя показывает
     * либо меню входа, либо основное меню управления товарами.
     */
    public void start() {
        while (true) {
            if (!userController.isAuthenticated()) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
    }

    /**
     * Отображает меню аутентификации (вход/регистрация/выход).
     */
    private void showAuthMenu() {
        String menu = """
            
            === ДОСТУП ===
            1. Войти
            2. Зарегистрироваться
            0. Выход
            """;
        System.out.print(menu + "Ваш выбор: "); String choice = scanner.nextLine();

        switch (choice) {
            case MENU_LOGIN -> login();
            case MENU_REGISTER -> register();
            case MENU_EXIT -> exitApp();
            default -> System.out.println("Неверный выбор");
        }
    }

    /**
     * Отображает основное меню приложения.
     * Доступные операции зависят от роли пользователя (например, только ADMIN может добавлять и удалять товары).
     */
    private void showMainMenu() {
        User currentUser = userController.currentUser();
        String menu = """
            
            === МЕНЮ ===
            1. Добавить товар
            2. Изменить товар
            3. Удалить товар
            4. Просмотреть все товары
            5. Поиск товаров
            6. Выйти из аккаунта
            0. Выход
            """;
        System.out.print(menu + "Выбор: ");  String choice = scanner.nextLine();

        switch (choice) {
            case MENU_ADD_PRODUCT -> add(currentUser);
            case MENU_UPDATE_PRODUCT -> update(currentUser);
            case MENU_DELETE_PRODUCT -> delete(currentUser);
            case MENU_SHOW_ALL -> productController.getAllProducts().forEach(System.out::println);
            case MENU_SEARCH -> search();
            case MENU_LOGOUT -> userController.logout();
            case MENU_EXIT_APP -> exitApp();
            default -> System.out.println("Неверный выбор");
        }
    }

    /**
     * Завершает работу приложения.
     */
    private void exitApp() {
        System.out.println("Данные сохранены. Выход...");
        System.exit(0);
    }

    /**
     * Регистрирует нового пользователя.
     * Первый пользователь в системе получает роль ADMIN автоматически.
     * Если логин уже занят, регистрация не выполняется.
     */
    private void register() {
        System.out.print("Желаемый логин: "); String login = scanner.nextLine();
        System.out.print("Пароль: "); String pass = scanner.nextLine();
        System.out.print("Роль (ADMIN/USER) (Enter для USER, первый пользователь в системе - ADMIN): "); String role = scanner.nextLine();
        boolean ok = userController.register(login, pass, role.isEmpty() ? null : role);
        if (!ok) {
            System.out.println("Регистрация не удалась (возможно логин занят).");
        } else {
            System.out.println("Регистрация прошла успешно. Войдите в систему.");
        }
    }

    /**
     * Выполняет вход пользователя по логину и паролю.
     */
    private void login() {
        System.out.print("Логин: "); String login = scanner.nextLine();
        System.out.print("Пароль: "); String pass = scanner.nextLine();
        if (!userController.login(login, pass)) {
            System.out.println("Ошибка входа!");
        }
    }

    /**
     * Добавление нового товара (только для ADMIN)
     */
    private void add(User user) {
        try {
            authService.checkAdmin(user);
            String name = readNonEmptyString(scanner, "Название: ");
            String cat = readNonEmptyString(scanner, "Категория: ");
            String brand = readNonEmptyString(scanner, "Бренд: ");
            BigDecimal price = readBigDecimal(scanner, "Цена: ", false);
            String desc = readNonEmptyString(scanner, "Описание: ");
            Product product = new Product(name, cat, brand, price, desc);
            productController.addProduct(product);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Обновление товара по id (только для ADMIN)
     */
    private void update(User user) {
        try {
            authService.checkAdmin(user);
            Long id = readLongId(scanner, "ID товара: ");
            String name = readNonEmptyString(scanner, "Новое название: ");
            String cat = readNonEmptyString(scanner, "Новая категория: ");
            String brand = readNonEmptyString(scanner, "Новый бренд: ");
            BigDecimal price = readBigDecimal(scanner, "Новая цена: ", false);
            String desc = readNonEmptyString(scanner, "Новое описание: ");
            productController.updateProduct(id, name, cat, brand, price, desc);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Удаление товара по id (только для ADMIN)
     */
    private void delete(User user) {
        try {
            authService.checkAdmin(user);
            List<Product> allProducts = productController.getAllProducts();
            if (allProducts.isEmpty()) {
                System.out.println("Нет товаров для удаления.");
                return;
            }
            System.out.println("Список товаров:");
            allProducts.forEach(p -> System.out.println(p.toString()));

            Long id = readLongId(scanner, "Введите ID товара для удаления: ");
            productController.deleteProduct(id);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Поиск товаров по фильтрам: ключевое слово, категория, бренд, диапазон цен
     */
    private void search() {
        System.out.print("Ключевое слово (Enter если нет): "); String kw = scanner.nextLine();
        System.out.print("Категория (Enter если нет): "); String cat = scanner.nextLine();
        System.out.print("Бренд (Enter если нет): "); String brand = scanner.nextLine();
        BigDecimal minPrice = readBigDecimal(scanner, "Мин. цена (Enter если нет): ", true);
        BigDecimal maxPrice = readBigDecimal(scanner, "Макс. цена (Enter если нет): ", true);

        SearchFilter f = new SearchFilter(
                kw.isEmpty() ? null : kw,
                cat.isEmpty() ? null : cat,
                brand.isEmpty() ? null : brand,
                minPrice,
                maxPrice
        );

        List<Product> res = metricService.measureExecutionTime(
                () -> productController.searchProducts(f),
                "Поиск товаров"
        );
        System.out.println("Найдено " + res.size() + " товаров");
        res.forEach(System.out::println);

    }

}
