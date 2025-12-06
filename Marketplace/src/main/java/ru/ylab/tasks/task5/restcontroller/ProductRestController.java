package ru.ylab.tasks.task5.restcontroller;

import org.mapstruct.factory.Mappers;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ylab.tasks.task5.audit.annotation.Auditable;
import ru.ylab.tasks.task5.constant.ResponseMessages;
import ru.ylab.tasks.task5.dto.mapper.ProductMapper;
import ru.ylab.tasks.task5.dto.request.product.ProductCreateRequest;
import ru.ylab.tasks.task5.dto.request.product.ProductDeleteRequest;
import ru.ylab.tasks.task5.dto.request.product.ProductSearchRequest;
import ru.ylab.tasks.task5.dto.request.product.ProductUpdateRequest;
import ru.ylab.tasks.task5.dto.response.product.*;
import ru.ylab.tasks.task5.exception.AccessDeniedException;
import ru.ylab.tasks.task5.model.Product;
import ru.ylab.tasks.task5.security.AuthService;
import ru.ylab.tasks.task5.service.product.ProductService;
import ru.ylab.tasks.task5.util.ParseUtils;
import ru.ylab.tasks.task5.util.ResponseHelper;
import ru.ylab.tasks.task5.util.SearchFilter;
import ru.ylab.tasks.task5.util.validation.ProductValidator;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static ru.ylab.tasks.task5.constant.ResponseMessages.*;

/**
 * REST контроллер для управления продуктами в маркетплейсе.
 * Обрабатывает запросы на создание, обновление, удаление, поиск и получение продуктов.
 * Требует аутентификации пользователя для большинства операций.
 */
@RestController
@RequestMapping("/marketplace/products")
public class ProductRestController {

    private final AuthService authService;
    private final ProductService productService;
    private final ProductValidator productValidator;
    private final ResponseHelper responseHelper;
    private final ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

    public ProductRestController(AuthService authService,
                                 ProductService productService,
                                 ProductValidator productValidator,
                                 ResponseHelper responseHelper) {
        this.authService = authService;
        this.productService = productService;
        this.productValidator = productValidator;
        this.responseHelper = responseHelper;
    }

    /**
     * Создает новый продукт в системе.
     * Требует аутентификации и прав администратора.
     * @param dto DTO с данными для создания продукта
     * @return ResponseEntity с результатом операции
     */
    @Auditable(action = "product_create")
    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@RequestBody ProductCreateRequest dto) {

        ResponseEntity<?> authError = checkAuth();
        if (authError != null) return authError;

        ResponseEntity<?> roleError = checkRole();
        if (roleError != null) return roleError;

        List<String> dtoError = productValidator.validateCreate(dto);
        if (!dtoError.isEmpty()) {
            return responseHelper.badRequest(VALIDATION_FAILED, String.join(", ", dtoError));
        }

        Product product;
        try {
            product = productMapper.toEntity(dto);
        } catch (Exception e) {
            return responseHelper.badRequest(INVALID_DATA, e.getMessage());
        }

        try {
            productService.create(product);
        } catch (Exception e) {
            return responseHelper.serverError(PRODUCT_CREATE_FAILED, e.getMessage());
        }

        return responseHelper.created(new ProductCreatedResponse(product.getId(), PRODUCT_CREATE_SUCCESS));
    }

    /**
     * Удаляет продукт по идентификатору.
     * Требует аутентификации и прав администратора.
     * @param dto DTO с идентификатором продукта для удаления
     * @return ResponseEntity с результатом операции
     */
    @Auditable(action = "product_delete")
    @PostMapping("/delete")
    public ResponseEntity<?> deleteProduct(@RequestBody ProductDeleteRequest dto) {

        ResponseEntity<?> authError = checkAuth();
        if (authError != null) return authError;

        ResponseEntity<?> roleError = checkRole();
        if (roleError != null) return roleError;

        List<String> idError = productValidator.validateDelete(dto);
        if (!idError.isEmpty()) {
            return responseHelper.badRequest(PRODUCT_ID_MISSING, String.join(", ", idError));
        }

        try {
            productService.delete(dto.getId());
        } catch (Exception e) {
            return responseHelper.serverError(PRODUCT_DELETE_FAILED, e.getMessage());
        }

        return responseHelper.ok(new ProductDeletedResponse(dto.getId(), PRODUCT_DELETED_SUCCESS));
    }

    /**
     * Возвращает список всех продуктов в системе.
     * Требует аутентификации пользователя.
     * @return ResponseEntity со списком продуктов
     */
    @GetMapping(value = "/list")
    public ResponseEntity<?> getAllProducts() {

        if (checkAuth() != null) {
            return responseHelper.unauthorized(USER_UNAUTHORIZED, "User must be logged in");
        }

        List<Product> products;
        try {
            products = productService.getAll();
        } catch (Exception e) {
            return responseHelper.serverError(PRODUCT_SEARCH_FAILED, e.getMessage());
        }

        List<ProductResponse> responseList = products.stream()
                .map(productMapper::toResponse)
                .toList();

        return responseHelper.ok(new ProductListResponse(responseList));
    }

    /**
     * Выполняет поиск продуктов по заданным критериям.
     * Требует аутентификации пользователя.
     * @param dto DTO с критериями поиска (ключевые слова, категория, бренд, диапазон цен)
     * @return ResponseEntity со списком найденных продуктов
     */
    @PostMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestBody ProductSearchRequest dto) {

        ResponseEntity<?> authError = checkAuth();
        if (authError != null) return authError;

        List<String> validationError = productValidator.validateSearch(dto);
        if (!validationError.isEmpty()) {
            return responseHelper.badRequest(PRODUCT_INVALID_MIN_MAX_PRICE, String.join(", ", validationError));
        }


                BigDecimal min = ParseUtils.parseBigDecimal(dto.getMinPrice());
        BigDecimal max = ParseUtils.parseBigDecimal(dto.getMaxPrice());

        SearchFilter filter = new SearchFilter(
                dto.getKeyword(),
                dto.getCategory(),
                dto.getBrand(),
                min,
                max
        );

        List<Product> products;
        try {
            products = productService.search(filter);
        } catch (Exception e) {
            return responseHelper.serverError(PRODUCT_SEARCH_FAILED, e.getMessage());
        }

        List<ProductResponse> responseList = products.stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());

        return responseHelper.ok(new ProductSearchResponse(responseList));
    }

    /**
     * Обновляет данные существующего продукта.
     * Требует аутентификации и прав администратора.
     * @param dto DTO с данными для обновления продукта
     * @return ResponseEntity с результатом операции
     */
    @Auditable(action = "product_update")
    @PostMapping("/update")
    public ResponseEntity<?> updateProduct(@RequestBody ProductUpdateRequest dto) {

        ResponseEntity<?> authError = checkAuth();
        if (authError != null) return authError;

        ResponseEntity<?> roleError = checkRole();
        if (roleError != null) return roleError;

        List<String> updateErrors = productValidator.validateUpdate(dto);
        if (!updateErrors.isEmpty()) {
            return responseHelper.badRequest(VALIDATION_FAILED, String.valueOf(updateErrors));
        }

        try {
            productService.update(
                    dto.getId(),
                    dto.getName(),
                    dto.getCategory(),
                    dto.getBrand(),
                    new BigDecimal(dto.getPrice()),
                    dto.getDescription()
            );
        } catch (Exception e) {
            return responseHelper.serverError(ResponseMessages.PRODUCT_UPDATE_FAILED, e.getMessage());
        }

        return responseHelper.ok(new ProductUpdatedResponse(dto.getId(), PRODUCT_UPDATED_SUCCESS));
    }

    /**
     * Проверяет аутентификацию текущего пользователя, обращаясь к authService.
     * @return ResponseEntity с ошибкой если пользователь не аутентифицирован, иначе null
     */
    private ResponseEntity<?> checkAuth() {
        if (!authService.isAuthenticated()) {
            return responseHelper.unauthorized(USER_UNAUTHORIZED, "User must be logged in");
        }
        return null;
    }

    /**
     * Проверяет наличие прав администратора у текущего пользователя, обращаясь к authService.
     * @return ResponseEntity с ошибкой если пользователь не администратор, иначе null
     */
    private ResponseEntity<?> checkRole() {
        try {
            authService.checkAdmin(authService.getCurrentUser());
            return null;
        } catch (AccessDeniedException e) {
            return responseHelper.forbidden(USER_FORBIDDEN, String.valueOf(e.getMessage()));
        }
    }

}
