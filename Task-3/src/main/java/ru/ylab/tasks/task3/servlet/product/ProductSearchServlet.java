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
import ru.ylab.tasks.task3.dto.request.product.ProductSearchRequest;
import ru.ylab.tasks.task3.dto.response.common.ErrorResponse;
import ru.ylab.tasks.task3.dto.response.product.ProductResponse;
import ru.ylab.tasks.task3.dto.response.product.ProductSearchResponse;
import ru.ylab.tasks.task3.model.Product;
import ru.ylab.tasks.task3.util.ParseUtils;
import ru.ylab.tasks.task3.util.SearchFilter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static ru.ylab.tasks.task3.constant.ResponseMessages.*;

/**
 * Сервлет для поиска продуктов в маркетплейсе по заданным критериям.
 * Обрабатывает POST запросы по пути "/marketplace/products/search".
 * Требует аутентификации пользователя для выполнения операции.
 */
@WebServlet("/marketplace/products/search")
public class ProductSearchServlet extends HttpServlet {

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
     * Обрабатывает POST запрос на поиск продуктов.
     * Выполняет последовательно: аутентификацию, парсинг DTO, валидацию цен,
     * создание фильтра, поиск продуктов и отправку результата.
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

        ProductSearchRequest filterDto = parseSearchRequest(req, resp);
        if (filterDto == null) {
            return;
        }

        if (!validatePriceRange(filterDto, resp)) {
            return;
        }

        SearchFilter searchFilter = createSearchFilter(filterDto);

        List<Product> products = searchProducts(searchFilter, resp);
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
                    new ErrorResponse(USER_UNAUTHORIZED, List.of("Пользователь должен войти")));
            return false;
        }
        return true;
    }

    /**
     * Парсит DTO запроса на поиск продуктов из тела HTTP запроса.
     * @param req HTTP запрос
     * @param resp HTTP ответ для отправки ошибки в случае неудачи
     * @return DTO запроса или null в случае ошибки парсинга
     */
    private ProductSearchRequest parseSearchRequest(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            return objectMapper.readValue(req.getInputStream(), ProductSearchRequest.class);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(ResponseMessages.INVALID_JSON, List.of(e.getMessage())));
            return null;
        }
    }

    /**
     * Валидирует корректность диапазона цен (minPrice <= maxPrice).
     * @param filterDto DTO запроса на поиск продуктов
     * @param resp HTTP ответ для отправки ошибки в случае неудачи
     * @return true если диапазон цен корректен, false в противном случае
     */
    private boolean validatePriceRange(ProductSearchRequest filterDto, HttpServletResponse resp) throws IOException {
        BigDecimal min = ParseUtils.parseBigDecimal(filterDto.getMinPrice());
        BigDecimal max = ParseUtils.parseBigDecimal(filterDto.getMaxPrice());

        if (min != null && max != null && min.compareTo(max) > 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(
                    resp.getWriter(),
                    new ErrorResponse(ResponseMessages.VALIDATION_FAILED,
                            List.of(PRODUCT_INVALID_MIN_MAX_PRICE))
            );
            return false;
        }
        return true;
    }

    /**
     * Создает объект фильтра для поиска на основе DTO запроса.
     * @param filterDto DTO запроса на поиск продуктов
     * @return объект SearchFilter с параметрами поиска
     */
    private SearchFilter createSearchFilter(ProductSearchRequest filterDto) {
        BigDecimal min = ParseUtils.parseBigDecimal(filterDto.getMinPrice());
        BigDecimal max = ParseUtils.parseBigDecimal(filterDto.getMaxPrice());

        return new SearchFilter(
                filterDto.getKeyword(),
                filterDto.getCategory(),
                filterDto.getBrand(),
                min,
                max
        );
    }

    /**
     * Выполняет поиск продуктов по заданному фильтру.
     * @param searchFilter фильтр для поиска продуктов
     * @param resp HTTP ответ для отправки ошибки в случае неудачи
     * @return список найденных продуктов или null в случае ошибки
     */
    private List<Product> searchProducts(SearchFilter searchFilter, HttpServletResponse resp) throws IOException {
        try {
            return productController.searchProducts(searchFilter);
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
     * Отправляет успешный ответ с результатами поиска.
     * @param responseList список DTO ответов с найденными продуктами
     * @param resp HTTP ответ
     */
    private void sendSuccessResponse(List<ProductResponse> responseList, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(), new ProductSearchResponse(responseList));
    }

}
