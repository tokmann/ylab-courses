package ru.ylab.tasks.task3.util.validation;

import ru.ylab.tasks.task3.dto.request.product.ProductCreateRequest;
import ru.ylab.tasks.task3.dto.request.product.ProductUpdateRequest;

import java.util.ArrayList;
import java.util.List;

public final class ProductValidator {

    private ProductValidator() {}

    public static List<String> validateCreate(ProductCreateRequest dto) {
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

    public static List<String> validateUpdate(ProductUpdateRequest dto) {
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
}
