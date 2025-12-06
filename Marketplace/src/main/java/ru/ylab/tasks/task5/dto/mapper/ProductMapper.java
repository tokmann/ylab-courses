package ru.ylab.tasks.task5.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ylab.tasks.task5.dto.request.product.ProductCreateRequest;
import ru.ylab.tasks.task5.dto.response.product.ProductResponse;
import ru.ylab.tasks.task5.model.Product;

import java.math.BigDecimal;

/**
 * Маппер для преобразования между сущностью Product и DTO.
 * Использует MapStruct для автоматического преобразования объектов.
 */
@Mapper(imports = {BigDecimal.class})
public interface ProductMapper {

    /**
     * Преобразует DTO запроса на создание продукта в сущность Product.
     * Игнорирует поле id и преобразует строковую цену в BigDecimal.
     * @param dto DTO запроса на создание продукта
     * @return сущность Product
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "price", expression = "java(new BigDecimal(dto.getPrice()))")
    Product toEntity(ProductCreateRequest dto);

    /**
     * Преобразует сущность Product в DTO ответа.
     * @param product сущность Product
     * @return DTO ответа с информацией о продукте
     */
    ProductResponse toResponse(Product product);
}