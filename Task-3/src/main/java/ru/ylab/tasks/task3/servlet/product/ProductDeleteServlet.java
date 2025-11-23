package ru.ylab.tasks.task3.servlet.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ylab.tasks.task3.constant.ResponseMessages;
import ru.ylab.tasks.task3.controller.ProductController;
import ru.ylab.tasks.task3.controller.UserController;
import ru.ylab.tasks.task3.dto.request.product.ProductDeleteRequest;
import ru.ylab.tasks.task3.dto.response.common.ErrorResponse;
import ru.ylab.tasks.task3.dto.response.product.ProductDeletedResponse;
import ru.ylab.tasks.task3.exception.AccessDeniedException;

import java.io.IOException;
import java.util.List;

import static ru.ylab.tasks.task3.constant.ResponseMessages.*;

@WebServlet("/marketplace/products/delete")
public class ProductDeleteServlet extends HttpServlet {

    private ProductController productController;
    private UserController userController;
    private final ObjectMapper objectMapper = new ObjectMapper();

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
                    new ErrorResponse(USER_UNAUTHORIZED, List.of("User must be logged in")));
            return;
        }

        try {
            userController.checkAdmin(userController.currentUser());
        } catch (AccessDeniedException e) {
            resp.setStatus(403);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(USER_FORBIDDEN, List.of(e.getMessage())));
            return;
        }

        ProductDeleteRequest dto;
        try {
            dto = objectMapper.readValue(req.getInputStream(), ProductDeleteRequest.class);
        } catch (Exception e) {
            resp.setStatus(400);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(INVALID_JSON, List.of(e.getMessage())));
            return;
        }

        if (dto.getId() == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(ResponseMessages.PRODUCT_ID_MISSING, List.of("")));
            return;
        }

        try {
            productController.deleteProduct(dto.getId());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(ResponseMessages.PRODUCT_DELETE_FAILED, List.of(e.getMessage())));
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(),
                new ProductDeletedResponse(dto.getId(), ResponseMessages.PRODUCT_DELETED_SUCCESS));
    }
}
