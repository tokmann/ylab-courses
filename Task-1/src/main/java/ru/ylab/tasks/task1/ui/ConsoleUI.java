package ru.ylab.tasks.task1.ui;

import ru.ylab.tasks.task1.constant.FileConstants;
import ru.ylab.tasks.task1.controller.ProductController;
import ru.ylab.tasks.task1.controller.UserController;
import ru.ylab.tasks.task1.model.Product;
import ru.ylab.tasks.task1.model.User;
import ru.ylab.tasks.task1.repository.InMemoryProductRepository;
import ru.ylab.tasks.task1.repository.InMemoryUserRepository;
import ru.ylab.tasks.task1.repository.ProductRepository;
import ru.ylab.tasks.task1.repository.UserRepository;
import ru.ylab.tasks.task1.security.AuthService;
import ru.ylab.tasks.task1.service.AuditService;
import ru.ylab.tasks.task1.service.ProductService;
import ru.ylab.tasks.task1.service.persistence.ProductFileService;
import ru.ylab.tasks.task1.service.persistence.UserFileService;
import ru.ylab.tasks.task1.util.SearchFilter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import static ru.ylab.tasks.task1.util.ConsoleUtils.*;

/**
 * Консольный интерфейс для взаимодействия пользователя с системой управления товарами.
 * Реализует аутентификацию, регистрацию, операции CRUD над товарами и поиск.
 */
public class ConsoleUI {

    private final ProductController productController;
    private final UserController userController;
    private final AuthService authService;
    private final ProductFileService productFileService;
    private final UserFileService userFileService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private final Scanner scanner = new Scanner(System.in);

    public ConsoleUI(ProductController productController,
                     UserController userController,
                     AuthService authService,
                     ProductFileService productFileService,
                     UserFileService userFileService,
                     ProductRepository productRepository,
                     UserRepository userRepository) {
        this.productController = productController;
        this.userController = userController;
        this.authService = authService;
        this.productFileService = productFileService;
        this.userFileService = userFileService;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    /** Главный цикл работы приложения */
    public void start() {
        while (true) {
            if (!userController.isAuthenticated()) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private void showAuthMenu() {
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
        System.out.print(menu + "Ваш выбор: "); String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> login();
            case "2" -> register();
            case "0" -> exitApp();
            default -> System.out.println("Неверный выбор");
        }
    }

    private void showMainMenu() {
        User currentUser = userController.currentUser();
        String menu = """
            
            === ДОСТУП ===
            1. Войти
            2. Зарегистрироваться
            0. Выход
            """;
        System.out.print(menu + "Выбор: ");  String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> add(currentUser);
            case "2" -> update(currentUser);
            case "3" -> delete(currentUser);
            case "4" -> productController.getAllProducts().forEach(System.out::println);
            case "5" -> search();
            case "6" -> userController.logout();
            case "0" -> exitApp();
            default -> System.out.println("Неверный выбор");
        }
    }

    private void exitApp() {
        productFileService.saveProducts(productRepository.findAll());
        userFileService.saveUsers(userRepository.findAll());
        System.out.println("Данные сохранены. Выход...");
        System.exit(0);
    }

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
     * Обновление товара по UUID (только для ADMIN)
     */
    private void update(User user) {
        try {
            authService.checkAdmin(user);
            UUID id = readUUID(scanner, "ID товара: ");
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
     * Удаление товара по UUID (только для ADMIN)
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

            UUID id = readUUID(scanner, "Введите UUID товара для удаления: ");
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

        // Формирование фильтра поиска
        SearchFilter f = new SearchFilter(
                kw.isEmpty() ? null : kw,
                cat.isEmpty() ? null : cat,
                brand.isEmpty() ? null : brand,
                minPrice,
                maxPrice
        );

        long startTime = System.currentTimeMillis();
        List<Product> res = productController.searchProducts(f);
        long endTime = System.currentTimeMillis();

        res.forEach(System.out::println);
        System.out.print("Найдено " + res.size() + " товаров за " + (endTime - startTime) + " мс");
    }

}
