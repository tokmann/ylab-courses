package ru.ylab.tasks.task3.servlet.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mapstruct.factory.Mappers;
import ru.ylab.tasks.task3.controller.interfaces.IProductController;
import ru.ylab.tasks.task3.controller.interfaces.IUserController;
import ru.ylab.tasks.task3.dto.mapper.ProductMapper;
import ru.ylab.tasks.task3.dto.request.product.ProductCreateRequest;
import ru.ylab.tasks.task3.dto.response.common.ErrorResponse;
import ru.ylab.tasks.task3.dto.response.product.ProductCreatedResponse;
import ru.ylab.tasks.task3.exception.AccessDeniedException;
import ru.ylab.tasks.task3.model.Product;
import ru.ylab.tasks.task3.util.validation.ProductValidator;

import java.io.IOException;
import java.util.List;

import static ru.ylab.tasks.task3.constant.ResponseMessages.*;

/**
 * Сервлет для создания новых продуктов в маркетплейсе.
 * Обрабатывает POST запросы по пути "/marketplace/products/create".
 * Требует аутентификации и прав администратора для выполнения операции.
 */
@WebServlet("/marketplace/products/create")
public class ProductCreateServlet extends HttpServlet {

    private IProductController productController;
    private IUserController userController;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

    /**
     * Инициализирует сервлет, получая контроллеры из контекста приложения.
     */
    @Override
    public void init() {
        this.productController = (IProductController) getServletContext().getAttribute("productController");
        this.userController = (IUserController) getServletContext().getAttribute("userController");
    }

    /**
     * Обрабатывает POST запрос на создание нового продукта.
     * Выполняет последовательно: аутентификацию, авторизацию, парсинг DTO,
     * валидацию, преобразование в сущность и сохранение продукта.
     * @param req HTTP запрос
     * @param resp HTTP ответ
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (!authenticateUser(resp)) {
            return;
        }

        if (!authorizeAdmin(resp)) {
            return;
        }

        ProductCreateRequest dto = parseProductCreateRequest(req, resp);
        if (dto == null) {
            return;
        }

        if (!validateProductData(dto, resp)) {
            return;
        }

        Product product = convertToProductEntity(dto, resp);
        if (product == null) {
            return;
        }

        if (!createProduct(product, resp)) {
            return;
        }

        sendSuccessResponse(product, resp);
    }

    /**
     * Проверяет аутентификацию текущего пользователя.
     * @param resp HTTP ответ для отправки ошибки в случае неудачи
     * @return true если пользователь аутентифицирован, false в противном случае
     */
    private boolean authenticateUser(HttpServletResponse resp) throws IOException {
        if (!userController.isAuthenticated()) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(USER_UNAUTHORIZED, List.of("User must be logged in")));
            return false;
        }
        return true;
    }

    /**
     * Проверяет наличие прав администратора у текущего пользователя.
     * @param resp HTTP ответ для отправки ошибки в случае неудачи
     * @return true если пользователь имеет права администратора, false в противном случае
     */
    private boolean authorizeAdmin(HttpServletResponse resp) throws IOException {
        try {
            userController.checkAdmin(userController.currentUser());
            return true;
        } catch (AccessDeniedException e) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(USER_FORBIDDEN, List.of(e.getMessage())));
            return false;
        }
    }

    /**
     * Парсит DTO запроса на создание продукта из тела HTTP запроса.
     * @param req HTTP запрос
     * @param resp HTTP ответ для отправки ошибки в случае неудачи
     * @return DTO запроса или null в случае ошибки парсинга
     */
    private ProductCreateRequest parseProductCreateRequest(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            return objectMapper.readValue(req.getInputStream(), ProductCreateRequest.class);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(INVALID_JSON, List.of(e.getMessage())));
            return null;
        }
    }

    /**
     * Валидирует данные продукта из DTO запроса.
     * @param dto DTO запроса на создание продукта
     * @param resp HTTP ответ для отправки ошибки в случае неудачи
     * @return true если данные валидны, false в противном случае
     */
    private boolean validateProductData(ProductCreateRequest dto, HttpServletResponse resp) throws IOException {
        List<String> errors = ProductValidator.validateCreate(dto);
        if (!errors.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(VALIDATION_FAILED, errors));
            return false;
        }
        return true;
    }

    /**
     * Преобразует DTO запроса в сущность Product.
     * @param dto DTO запроса на создание продукта
     * @param resp HTTP ответ для отправки ошибки в случае неудачи
     * @return сущность Product или null в случае ошибки преобразования
     */
    private Product convertToProductEntity(ProductCreateRequest dto, HttpServletResponse resp) throws IOException {
        try {
            return productMapper.toEntity(dto);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(INVALID_DATA, List.of(e.getMessage())));
            return null;
        }
    }

    /**
     * Создает новый продукт в системе.
     * @param product сущность Product для создания
     * @param resp HTTP ответ для отправки ошибки в случае неудачи
     * @return true если продукт успешно создан, false в противном случае
     */
    private boolean createProduct(Product product, HttpServletResponse resp) throws IOException {
        try {
            productController.addProduct(product);
            return true;
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(PRODUCT_CREATE_FAILED, List.of(e.getMessage())));
            return false;
        }
    }

    /**
     * Отправляет успешный ответ с информацией о созданном продукте.
     * @param product созданная сущность Product
     * @param resp HTTP ответ
     * @throws IOException если произошла ошибка при записи в ответ
     */
    private void sendSuccessResponse(Product product, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_CREATED);
        objectMapper.writeValue(resp.getWriter(),
                new ProductCreatedResponse(product.getId(), PRODUCT_CREATE_SUCCESS));
    }
}
