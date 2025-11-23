package ru.ylab.tasks.task3.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import ru.ylab.tasks.task3.constant.Role;
import ru.ylab.tasks.task3.model.Product;
import ru.ylab.tasks.task3.security.IAuthService;
import ru.ylab.tasks.task3.service.audit.IAuditService;

import static ru.ylab.tasks.task3.constant.AuditMessages.*;

@Aspect
public class AuditAspect {

    private static IAuditService auditService;
    private static IAuthService authService;

    public static void setAuditService(IAuditService service) {
        auditService = service;
    }

    public static void setAuthService(IAuthService service) {
        authService = service;
    }

    // ProductController методы
    @AfterReturning("execution(* ru.ylab.tasks.task3.controller.ProductController.addProduct(..))")
    public void auditAddProduct(JoinPoint jp) {
        Product product = (Product) jp.getArgs()[0];
        auditService.log(String.format(PRODUCT_ADDED, product.getName(), authService.getCurrentUserLogin()));
    }

    @AfterReturning("execution(* ru.ylab.tasks.task3.controller.ProductController.updateProduct(..))")
    public void auditUpdateProduct(JoinPoint jp) {
        Long productId = (Long) jp.getArgs()[0];
        auditService.log(String.format(PRODUCT_UPDATED, productId, authService.getCurrentUserLogin()));
    }

    @AfterReturning("execution(* ru.ylab.tasks.task3.controller.ProductController.deleteProduct(..))")
    public void auditDeleteProduct(JoinPoint jp) {
        Long productId = (Long) jp.getArgs()[0];
        auditService.log(String.format(PRODUCT_DELETED, productId, authService.getCurrentUserLogin()));
    }

    @AfterReturning("execution(* ru.ylab.tasks.task3.controller.ProductController.searchProducts(..))")
    public void auditSearchProducts(JoinPoint jp) {
        auditService.log(String.format(PRODUCT_SEARCH, authService.getCurrentUserLogin()));
    }

    // UserController методы
    @AfterReturning(pointcut = "execution(* ru.ylab.tasks.task3.controller.UserController.login(..))", returning = "success")
    public void auditLogin(JoinPoint jp, boolean success) {
        String login = (String) jp.getArgs()[0];
        if (success) {
            auditService.log(String.format(LOGIN_SUCCESS, login));
        } else {
            auditService.log(String.format(LOGIN_FAILED, login));
        }
    }

    @Before("execution(* ru.ylab.tasks.task3.controller.UserController.logout())")
    public void auditLogout(JoinPoint jp) {
        if (authService.isAuthenticated()) {
            auditService.log(String.format(LOGOUT_SUCCESS, authService.getCurrentUserLogin()));
        }
    }

    @AfterReturning(pointcut = "execution(* ru.ylab.tasks.task3.controller.UserController.register(..))", returning = "success")
    public void auditRegister(JoinPoint jp, boolean success) {
        String login = (String) jp.getArgs()[0];
        String requestedRole = (String) jp.getArgs()[2];
        Role assignedRole = authService.determineAssignedRole(requestedRole);

        if (success) {
            auditService.log(String.format(USER_REGISTERED, login, assignedRole));
        } else {
            auditService.log(String.format(USER_REGISTER_FAILED, login));
        }
    }
}