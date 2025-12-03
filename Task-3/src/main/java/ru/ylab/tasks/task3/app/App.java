package ru.ylab.tasks.task3.app;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ru.ylab.tasks.task3.aop.AuditAspect;
import ru.ylab.tasks.task3.config.AppConfig;
import ru.ylab.tasks.task3.db.migration.LiquibaseRunner;

@WebListener
public class App implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        try {
            AppConfig config = new AppConfig();

            LiquibaseRunner liquibaseRunner = new LiquibaseRunner(config.dbConfig());
            liquibaseRunner.updateDatabase();

            AuditAspect.setAuditService(config.auditService());
            AuditAspect.setAuthService(config.authService());

            ctx.setAttribute("productController", config.productController());
            ctx.setAttribute("userController", config.userController());

            System.out.println("Приложение запущено успешно");

        } catch (Exception e) {
            System.err.println("Ошибка приложения: " + e.getMessage());
            e.printStackTrace();
        }
    }
}