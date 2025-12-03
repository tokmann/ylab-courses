package ru.ylab.tasks.task3.servlet.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ylab.tasks.task3.constant.ResponseMessages;
import ru.ylab.tasks.task3.controller.interfaces.IProductController;
import ru.ylab.tasks.task3.controller.interfaces.IUserController;
import ru.ylab.tasks.task3.dto.request.product.ProductDeleteRequest;
import ru.ylab.tasks.task3.dto.response.common.ErrorResponse;
import ru.ylab.tasks.task3.dto.response.product.ProductDeletedResponse;
import ru.ylab.tasks.task3.exception.AccessDeniedException;

import java.io.IOException;
import java.util.List;

import static ru.ylab.tasks.task3.constant.ResponseMessages.*;

/**
 * Сервлет для удаления продуктов из маркетплейса.
 * Обрабатывает POST запросы по пути "/marketplace/products/delete".
 * Требует аутентификации и прав администратора для выполнения операции.
 */
@WebServlet("/marketplace/products/delete")
public class ProductDeleteServlet extends HttpServlet {

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
     * Обрабатывает POST запрос на удаление продукта.
     * Выполняет последовательно: аутентификацию, авторизацию, парсинг DTO,
     * валидацию идентификатора и удаление продукта.
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

        ProductDeleteRequest dto = parseProductDeleteRequest(req, resp);
        if (dto == null) {
            return;
        }

        if (!validateProductId(dto, resp)) {
            return;
        }

        if (!deleteProduct(dto, resp)) {
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
     * Парсит DTO запроса на удаление продукта из тела HTTP запроса.
     * @param req HTTP запрос
     * @param resp HTTP ответ для отправки ошибки в случае неудачи
     * @return DTO запроса или null в случае ошибки парсинга
     */
    private ProductDeleteRequest parseProductDeleteRequest(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            return objectMapper.readValue(req.getInputStream(), ProductDeleteRequest.class);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(INVALID_JSON, List.of(e.getMessage())));
            return null;
        }
    }

    /**
     * Валидирует идентификатор продукта из DTO запроса.
     * @param dto DTO запроса на удаление продукта
     * @param resp HTTP ответ для отправки ошибки в случае неудачи
     * @return true если идентификатор валиден, false в противном случае
     */
    private boolean validateProductId(ProductDeleteRequest dto, HttpServletResponse resp) throws IOException {
        if (dto.getId() == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(ResponseMessages.PRODUCT_ID_MISSING, List.of("Product ID is required")));
            return false;
        }
        return true;
    }

    /**
     * Удаляет продукт из системы.
     *
     * @param dto DTO запроса на удаление продукта
     * @param resp HTTP ответ для отправки ошибки в случае неудачи
     * @return true если продукт успешно удален, false в противном случае
     */
    private boolean deleteProduct(ProductDeleteRequest dto, HttpServletResponse resp) throws IOException {
        try {
            productController.deleteProduct(dto.getId());
            return true;
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(ResponseMessages.PRODUCT_DELETE_FAILED, List.of(e.getMessage())));
            return false;
        }
    }

    /**
     * Отправляет успешный ответ с информацией об удаленном продукте.
     * @param dto DTO запроса на удаление продукта
     * @param resp HTTP ответ
     */
    private void sendSuccessResponse(ProductDeleteRequest dto, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(),
                new ProductDeletedResponse(dto.getId(), ResponseMessages.PRODUCT_DELETED_SUCCESS));
    }
}
