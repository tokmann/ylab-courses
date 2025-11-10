package ru.ylab.tasks.task1.ui;

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

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        AuditService audit = new AuditService();

        ProductRepository productRepository = new InMemoryProductRepository();
        ProductService productService = new ProductService(productRepository);

        UserRepository userRepository = new InMemoryUserRepository();
        AuthService authService = new AuthService(userRepository);

        ProductController productController = new ProductController(productService, audit);
        UserController userController = new UserController(authService, audit);

        // Загрузка сохранённых данных пользователей и товаров
        authService.loadFromFile();
        productService.loadFromFile();

        while (true) {
            // Если пользователь не вошёл — показываем меню доступа
            if (!userController.isAuthenticated()) {
                System.out.println("\n=== ДОСТУП ===");
                System.out.println("1. Войти");
                System.out.println("2. Зарегистрироваться");
                System.out.println("0. Выход");
                System.out.print("Выбор: ");
                String choice = scanner.nextLine();
                if ("1".equals(choice)) {
                    System.out.print("Логин: "); String login = scanner.nextLine();
                    System.out.print("Пароль: "); String pass = scanner.nextLine();
                    if (!userController.login(login, pass)) {
                        System.out.println("Ошибка входа!");
                        continue;
                    }
                } else if ("2".equals(choice)) {
                    System.out.print("Желаемый логин: "); String login = scanner.nextLine();
                    System.out.print("Пароль: "); String pass = scanner.nextLine();
                    System.out.print("Роль (ADMIN/USER) (Enter для USER, первый пользователь в системе - ADMIN): "); String role = scanner.nextLine();
                    boolean ok = userController.register(login, pass, role.isEmpty() ? null : role);
                    if (!ok) {
                        System.out.println("Регистрация не удалась (возможно логин занят).");
                        continue;
                    } else {
                        System.out.println("Регистрация прошла успешно. Войдите в систему.");
                        continue;
                    }
                } else if ("0".equals(choice)) {
                    saveData(productService, authService);
                    System.out.println("Выход...");
                    return;
                } else {
                    System.out.println("Неверный выбор");
                    continue;
                }
            }

            // Если пользователь вошел - показываем меню действий
            User currentUser = userController.currentUser();
            System.out.println("\n=== МЕНЮ ===");
            System.out.println("1. Добавить товар");
            System.out.println("2. Изменить товар");
            System.out.println("3. Удалить товар");
            System.out.println("4. Просмотреть все товары");
            System.out.println("5. Поиск товаров");
            System.out.println("6. Выйти из аккаунта");
            System.out.println("0. Выход");
            System.out.print("Ваш выбор: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> add(scanner, productController, currentUser);
                case "2" -> update(scanner, productController, currentUser);
                case "3" -> delete(scanner, productController, currentUser);
                case "4" -> productController.getAllProducts().forEach(System.out::println);
                case "5" -> search(scanner, productController);
                case "6" -> userController.logout();
                case "0" -> {
                    saveData(productService, authService);
                    return;
                }
                default -> System.out.println("Неверный выбор");
            }
        }
    }

    /**
     * Добавление нового товара (только для ADMIN)
     */
    private static void add(Scanner sc, ProductController ctrl, User user) {
        try {
            ctrl.checkAdmin(user);
            String name = readNonEmptyString(sc, "Название: ");
            String cat = readNonEmptyString(sc, "Категория: ");
            String brand = readNonEmptyString(sc, "Бренд: ");
            BigDecimal price = readBigDecimal(sc, "Цена: ", false);
            String desc = readNonEmptyString(sc, "Описание: ");
            ctrl.addProduct(name, cat, brand, price, desc);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Обновление товара по UUID (только для ADMIN)
     */
    private static void update(Scanner sc, ProductController ctrl, User user) {
        try {
            ctrl.checkAdmin(user);
            UUID id = readUUID(sc, "ID товара: ");
            String name = readNonEmptyString(sc, "Новое название: ");
            String cat = readNonEmptyString(sc, "Новая категория: ");
            String brand = readNonEmptyString(sc, "Новый бренд: ");
            BigDecimal price = readBigDecimal(sc, "Новая цена: ", false);
            String desc = readNonEmptyString(sc, "Новое описание: ");
            ctrl.updateProduct(id, name, cat, brand, price, desc);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Удаление товара по UUID (только для ADMIN)
     */
    private static void delete(Scanner sc, ProductController ctrl, User user) {
        try {
            ctrl.checkAdmin(user);
            List<Product> allProducts = ctrl.getAllProducts();
            if (allProducts.isEmpty()) {
                System.out.println("Нет товаров для удаления.");
                return;
            }
            System.out.println("Список товаров:");
            allProducts.forEach(p -> System.out.println(p.toString()));

            UUID id = readUUID(sc, "Введите UUID товара для удаления: ");
            ctrl.deleteProduct(id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Поиск товаров по фильтрам: ключевое слово, категория, бренд, диапазон цен
     */
    private static void search(Scanner sc, ProductController ctrl) {
        System.out.print("Ключевое слово (Enter если нет): "); String kw = sc.nextLine();
        System.out.print("Категория (Enter если нет): "); String cat = sc.nextLine();
        System.out.print("Бренд (Enter если нет): "); String brand = sc.nextLine();
        BigDecimal minPrice = readBigDecimal(sc, "Мин. цена (Enter если нет): ", true);
        BigDecimal maxPrice = readBigDecimal(sc, "Макс. цена (Enter если нет): ", true);

        // Формирование фильтра поиска
        SearchFilter f = new SearchFilter(
                kw.isEmpty() ? null : kw,
                cat.isEmpty() ? null : cat,
                brand.isEmpty() ? null : brand,
                minPrice,
                maxPrice
        );

        long startTime = System.currentTimeMillis();
        List<Product> res = ctrl.searchProducts(f);
        long endTime = System.currentTimeMillis();

        res.forEach(System.out::println);
        System.out.print("Найдено " + res.size() + " товаров за " + (endTime - startTime) + " мс");
    }

    /**
     * Сохраняет все данные перед завершением работы приложения
     */
    private static void saveData(ProductService productService, AuthService authService) {
        productService.saveToFile();
        authService.saveToFile();
        System.out.println("Данные сохранены.");
        System.out.println("Выход...");
    }

}
