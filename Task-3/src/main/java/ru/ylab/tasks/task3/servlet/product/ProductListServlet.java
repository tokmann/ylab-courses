package ru.ylab.tasks.task3.servlet.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mapstruct.factory.Mappers;
import ru.ylab.tasks.task3.constant.ResponseMessages;
import ru.ylab.tasks.task3.controller.interfaces.IProductController;
import ru.ylab.tasks.task3.controller.interfaces.IUserController;
import ru.ylab.tasks.task3.dto.mapper.ProductMapper;
import ru.ylab.tasks.task3.dto.response.common.ErrorResponse;
import ru.ylab.tasks.task3.dto.response.product.ProductListResponse;
import ru.ylab.tasks.task3.dto.response.product.ProductResponse;
import ru.ylab.tasks.task3.model.Product;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static ru.ylab.tasks.task3.constant.ResponseMessages.USER_UNAUTHORIZED;

/**
 * Сервлет для получения списка всех продуктов маркетплейса.
 * Обрабатывает GET запросы по пути "/marketplace/products/list".
 * Требует аутентификации пользователя для выполнения операции.
 */
@WebServlet("/marketplace/products/list")
public class ProductListServlet extends HttpServlet {

    private IProductController productController;
    private IUserController userController;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

    /**
     * Инициализирует сервлет, получая контроллеры из контекста приложения.
     */
    @Override
    public void init() {
        this.productController =  (IProductController) getServletContext().getAttribute("productController");
        this.userController = (IUserController) getServletContext().getAttribute("userController");
    }

    /**
     * Обрабатывает GET запрос на получение списка всех продуктов.
     * Выполняет последовательно: аутентификацию, получение продуктов,
     * преобразование в DTO и отправку ответа.
     * @param req HTTP запрос
     * @param resp HTTP ответ
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (!authenticateUser(resp)) {
            return;
        }

        List<Product> products = retrieveProducts(resp);
        if (products == null) {
            return;
        }

        List<ProductResponse> responseList = convertToResponseList(products);

        sendSuccessResponse(responseList, resp);
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
     * Получает список всех продуктов из системы.
     * @param resp HTTP ответ для отправки ошибки в случае неудачи
     * @return список продуктов или null в случае ошибки
     */
    private List<Product> retrieveProducts(HttpServletResponse resp) throws IOException {
        try {
            return productController.getAllProducts();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(ResponseMessages.PRODUCT_SEARCH_FAILED, List.of(e.getMessage())));
            return null;
        }
    }

    /**
     * Преобразует список сущностей Product в список DTO ответов.
     * @param products список сущностей Product
     * @return список DTO ответов ProductResponse
     */
    private List<ProductResponse> convertToResponseList(List<Product> products) {
        return products.stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Отправляет успешный ответ со списком продуктов.
     * @param responseList список DTO ответов с информацией о продуктах
     * @param resp HTTP ответ
     */
    private void sendSuccessResponse(List<ProductResponse> responseList, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(), new ProductListResponse(responseList));
    }
}
