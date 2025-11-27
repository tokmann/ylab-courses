package ru.ylab.tasks.task3.servlet.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ylab.tasks.task3.constant.ResponseMessages;
import ru.ylab.tasks.task3.controller.interfaces.IProductController;
import ru.ylab.tasks.task3.controller.interfaces.IUserController;
import ru.ylab.tasks.task3.dto.request.product.ProductUpdateRequest;
import ru.ylab.tasks.task3.dto.response.common.ErrorResponse;
import ru.ylab.tasks.task3.dto.response.product.ProductUpdatedResponse;
import ru.ylab.tasks.task3.exception.AccessDeniedException;
import ru.ylab.tasks.task3.util.validation.ProductValidator;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static ru.ylab.tasks.task3.constant.ResponseMessages.*;

/**
 * Сервлет для обновления существующих продуктов в маркетплейсе.
 * Обрабатывает POST запросы по пути "/marketplace/products/update".
 * Требует аутентификации и прав администратора для выполнения операции.
 */
@WebServlet("/marketplace/products/update")
public class ProductUpdateServlet extends HttpServlet {

    private IProductController productController;
    private IUserController userController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Инициализирует сервлет, получая контроллеры из контекста приложения.
     */
    @Override
    public void init() {
        this.productController = (IProductController) getServletContext().getAttribute("productController");
        this.userController = (IUserController) getServletContext().getAttribute("userController");
    }

    /**
     * Обрабатывает POST запрос на обновление продукта.
     * Выполняет последовательно: аутентификацию, авторизацию, парсинг DTO,
     * валидацию данных, преобразование цены и обновление продукта.
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

        ProductUpdateRequest dto = parseProductUpdateRequest(req, resp);
        if (dto == null) {
            return;
        }

        if (!validateProductData(dto, resp)) {
            return;
        }

        if (!updateProduct(dto, resp)) {
            return;
        }

        sendSuccessResponse(dto, resp);
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
     * Парсит DTO запроса на обновление продукта из тела HTTP запроса.
     * @param req HTTP запрос
     * @param resp HTTP ответ для отправки ошибки в случае неудачи
     * @return DTO запроса или null в случае ошибки парсинга
     */
    private ProductUpdateRequest parseProductUpdateRequest(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            return objectMapper.readValue(req.getInputStream(), ProductUpdateRequest.class);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(INVALID_JSON, List.of(e.getMessage())));
            return null;
        }
    }

    /**
     * Валидирует данные продукта из DTO запроса.
     * @param dto DTO запроса на обновление продукта
     * @param resp HTTP ответ для отправки ошибки в случае неудачи
     * @return true если данные валидны, false в противном случае
     */
    private boolean validateProductData(ProductUpdateRequest dto, HttpServletResponse resp) throws IOException {
        List<String> errors = ProductValidator.validateUpdate(dto);
        if (!errors.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(VALIDATION_FAILED, errors));
            return false;
        }
        return true;
    }

    /**
     * Обновляет продукт в системе.
     * @param dto DTO запроса на обновление продукта
     * @param resp HTTP ответ для отправки ошибки в случае неудачи
     * @return true если продукт успешно обновлен, false в противном случае
     */
    private boolean updateProduct(ProductUpdateRequest dto, HttpServletResponse resp) throws IOException {
        try {
            productController.updateProduct(
                    dto.getId(),
                    dto.getName(),
                    dto.getCategory(),
                    dto.getBrand(),
                    new BigDecimal(dto.getPrice()),
                    dto.getDescription()
            );
            return true;
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(ResponseMessages.PRODUCT_CREATE_FAILED, List.of(e.getMessage())));
            return false;
        }
    }

    /**
     * Отправляет успешный ответ с информацией об обновленном продукте.
     * @param dto DTO запроса на обновление продукта
     * @param resp HTTP ответ
     */
    private void sendSuccessResponse(ProductUpdateRequest dto, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(),
                new ProductUpdatedResponse(dto.getId(), ResponseMessages.PRODUCT_UPDATED_SUCCESS));
    }

}
