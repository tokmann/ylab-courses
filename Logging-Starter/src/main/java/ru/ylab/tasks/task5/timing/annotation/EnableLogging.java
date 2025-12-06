package ru.ylab.tasks.task5.timing.annotation;

import org.springframework.context.annotation.Import;
import ru.ylab.tasks.task5.timing.LoggingConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(LoggingConfiguration.class)
public @interface EnableLogging {}

