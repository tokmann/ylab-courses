package ru.ylab.tasks.task3.servlet.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mapstruct.factory.Mappers;
import ru.ylab.tasks.task3.constant.ResponseMessages;
import ru.ylab.tasks.task3.controller.ProductController;
import ru.ylab.tasks.task3.controller.UserController;
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

@WebServlet("/marketplace/products/search")
public class ProductSearchServlet extends HttpServlet {

    private ProductController productController;
    private UserController userController;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

    @Override
    public void init() {
        this.productController = (ProductController) getServletContext().getAttribute("productController");
        this.userController = (UserController) getServletContext().getAttribute("userController");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (!userController.isAuthenticated()) {
            resp.setStatus(401);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(USER_UNAUTHORIZED, List.of("Пользователь должен войти")));
            return;
        }

        ProductSearchRequest filterDto;
        try {
            filterDto = objectMapper.readValue(req.getInputStream(), ProductSearchRequest.class);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(ResponseMessages.INVALID_JSON, List.of(e.getMessage())));
            return;
        }

        BigDecimal min = ParseUtils.parseBigDecimal(filterDto.getMinPrice());
        BigDecimal max = ParseUtils.parseBigDecimal(filterDto.getMaxPrice());

        if (min != null && max != null && min.compareTo(max) > 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(
                    resp.getWriter(),
                    new ErrorResponse(ResponseMessages.VALIDATION_FAILED,
                            List.of(PRODUCT_INVALID_MIN_MAX_PRICE))
            );
            return;
        }

        var searchFilter = new SearchFilter(
                filterDto.getKeyword(),
                filterDto.getCategory(),
                filterDto.getBrand(),
                min,
                max
        );

        List<Product> products;
        try {
            products = productController.searchProducts(searchFilter);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(ResponseMessages.PRODUCT_SEARCH_FAILED, List.of(e.getMessage())));
            return;
        }

        List<ProductResponse> responseList = products.stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());

        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(),
                new ProductSearchResponse(responseList));
    }

}
