package ru.ylab.tasks.task4.restcontroller;

import jakarta.servlet.http.HttpServletResponse;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ylab.tasks.task4.constant.ResponseMessages;
import ru.ylab.tasks.task4.dto.mapper.ProductMapper;
import ru.ylab.tasks.task4.dto.request.product.ProductCreateRequest;
import ru.ylab.tasks.task4.dto.request.product.ProductDeleteRequest;
import ru.ylab.tasks.task4.dto.request.product.ProductSearchRequest;
import ru.ylab.tasks.task4.dto.request.product.ProductUpdateRequest;
import ru.ylab.tasks.task4.dto.response.common.ErrorResponse;
import ru.ylab.tasks.task4.dto.response.product.*;
import ru.ylab.tasks.task4.exception.AccessDeniedException;
import ru.ylab.tasks.task4.model.Product;
import ru.ylab.tasks.task4.security.IAuthService;
import ru.ylab.tasks.task4.service.product.IProductService;
import ru.ylab.tasks.task4.util.ParseUtils;
import ru.ylab.tasks.task4.util.ResponseHelper;
import ru.ylab.tasks.task4.util.SearchFilter;
import ru.ylab.tasks.task4.util.validation.ProductValidator;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static ru.ylab.tasks.task4.constant.ResponseMessages.*;

@RestController
@RequestMapping("/marketplace/products")
public class ProductRestController {

    private final IAuthService authService;
    private final IProductService productService;
    private final ProductValidator productValidator;
    private final ResponseHelper responseHelper;
    private final ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

    public ProductRestController(IAuthService authService,
                                 IProductService productService,
                                 ProductValidator productValidator,
                                 ResponseHelper responseHelper) {
        this.authService = authService;
        this.productService = productService;
        this.productValidator = productValidator;
        this.responseHelper = responseHelper;
    }


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


    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
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


    private ResponseEntity<?> checkAuth() {
        if (!authService.isAuthenticated()) {
            return responseHelper.unauthorized(USER_UNAUTHORIZED, "User must be logged in");
        }
        return null;
    }


    private ResponseEntity<?> checkRole() {
        try {
            authService.checkAdmin(authService.getCurrentUser());
            return null;
        } catch (AccessDeniedException e) {
            return responseHelper.forbidden(USER_FORBIDDEN, String.valueOf(e.getMessage()));
        }
    }

}
