package ru.ylab.tasks.task5.util.validation;

import org.springframework.stereotype.Component;
import ru.ylab.tasks.task5.dto.request.product.ProductCreateRequest;
import ru.ylab.tasks.task5.dto.request.product.ProductDeleteRequest;
import ru.ylab.tasks.task5.dto.request.product.ProductSearchRequest;
import ru.ylab.tasks.task5.dto.request.product.ProductUpdateRequest;
import ru.ylab.tasks.task5.util.ParseUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static ru.ylab.tasks.task5.constant.ResponseMessages.PRODUCT_INVALID_MIN_MAX_PRICE;

/**
 * Утилитарный класс для валидации данных продуктов.
 * Предоставляет статические методы для проверки корректности данных
 * при создании и обновлении продуктов.
 */
@Component
public class ProductValidator {

    /**
     * Валидирует данные для создания нового продукта.
     * Проверяет обязательные поля и корректность формата цены.
     * @param dto DTO запроса на создание продукта
     * @return список ошибок валидации. Если список пуст, данные считаются валидными
     */
    public List<String> validateCreate(ProductCreateRequest dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getName() == null || dto.getName().isBlank())
            errors.add("Name must not be empty");

        if (dto.getCategory() == null || dto.getCategory().isBlank())
            errors.add("Category must not be empty");

        if (dto.getBrand() == null || dto.getBrand().isBlank())
            errors.add("Brand must not be empty");

        if (dto.getPrice() == null || dto.getPrice().isBlank())
            errors.add("Price must not be empty");
        else {
            try {
                new java.math.BigDecimal(dto.getPrice());
            } catch (Exception e) {
                errors.add("Price must be a valid decimal number");
            }
        }

        return errors;
    }

    /**
     * Валидирует данные для обновления существующего продукта.
     * Проверяет обязательные поля, корректность формата цены и наличие идентификатора продукта.
     * @param dto DTO запроса на обновление продукта
     * @return список ошибок валидации. Если список пуст, данные считаются валидными
     */
    public List<String> validateUpdate(ProductUpdateRequest dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getName() == null || dto.getName().isBlank())
            errors.add("Name must not be empty");

        if (dto.getCategory() == null || dto.getCategory().isBlank())
            errors.add("Category must not be empty");

        if (dto.getBrand() == null || dto.getBrand().isBlank())
            errors.add("Brand must not be empty");

        if (dto.getPrice() == null || dto.getPrice().isBlank())
            errors.add("Price must be greater than zero");
        else {
            try {
                new java.math.BigDecimal(dto.getPrice());
            } catch (Exception e) {
                errors.add("Price must be a valid decimal number");
            }
        }

        if (dto.getId() == null)
            errors.add("No product id");

        return errors;
    }

    /**
     * Валидирует данные для удаления продукта.
     * Проверяет наличие идентификатора продукта.
     * @param dto DTO запроса на удаление продукта
     * @return список ошибок валидации. Если список пуст, данные считаются валидными
     */
    public List<String> validateDelete(ProductDeleteRequest dto) {
        List<String> errors = new ArrayList<>();
        if (dto == null || dto.getId() == null) {
            errors.add("Product ID is required");
        }
        return errors;
    }

    /**
     * Валидирует данные для поиска продуктов.
     * Проверяет корректность диапазона цен (min <= max).
     * @param dto DTO запроса на поиск продуктов
     * @return список ошибок валидации. Если список пуст, данные считаются валидными
     */
    public List<String> validateSearch(ProductSearchRequest dto) {
        List<String> errors = new ArrayList<>();
        BigDecimal min = ParseUtils.parseBigDecimal(dto.getMinPrice());
        BigDecimal max = ParseUtils.parseBigDecimal(dto.getMaxPrice());

        if (min != null && max != null && min.compareTo(max) > 0) {
            errors.add(PRODUCT_INVALID_MIN_MAX_PRICE);
        }
        return errors;
    }
}
