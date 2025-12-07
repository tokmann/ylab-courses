package ru.ylab.tasks.task5.audit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для пометки методов, которые требуют аудита.
 * Применяется к методам, выполнение которых должно регистрироваться в системе аудита.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
    /**
     * Возвращает описание действия, которое будет зарегистрировано в аудите.
     * @return строка с описанием действия
     */
    String action();
}

