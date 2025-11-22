package ru.ylab.tasks.task3.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ylab.tasks.task3.dto.request.product.ProductCreateRequest;
import ru.ylab.tasks.task3.dto.response.product.ProductResponse;
import ru.ylab.tasks.task3.model.Product;

import java.math.BigDecimal;

@Mapper(imports = {BigDecimal.class})
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "price", expression = "java(new BigDecimal(dto.getPrice()))")
    Product toEntity(ProductCreateRequest dto);

    ProductResponse toResponse(Product product);
}
