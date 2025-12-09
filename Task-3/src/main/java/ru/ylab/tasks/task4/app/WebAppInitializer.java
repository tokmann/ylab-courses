package ru.ylab.tasks.task4.app;

import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;
import ru.ylab.tasks.task4.config.AppConfiguration;
import ru.ylab.tasks.task4.config.LiquibaseConfiguration;

/**
 * Класс инициализации веб-приложения Spring MVC.
 * Настраивает корневой контекст приложения, сервлеты и фильтры.
 */
public class WebAppInitializer implements WebApplicationInitializer {

    /**
     * Настраивает приложение при его запуске.
     * Регистрирует конфигурационные классы, слушатели, фильтры и сервлеты.
     * @param servletContext контекст сервлета для настройки
     */
    @Override
    public void onStartup(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(AppConfiguration.class, LiquibaseConfiguration.class);
        servletContext.addListener(new ContextLoaderListener(context));

        FilterRegistration.Dynamic encodingFilter = servletContext.addFilter("encodingFilter",
                CharacterEncodingFilter.class);
        encodingFilter.setInitParameter("encoding", "UTF-8");
        encodingFilter.setInitParameter("forceEncoding", "true");
        encodingFilter.addMappingForUrlPatterns(null, true, "/*");

        DispatcherServlet ds = new DispatcherServlet(context);
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", ds);
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }
}

