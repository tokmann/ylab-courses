package ru.ylab.tasks.task5.audit.handler;

import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 * Реализация AuditHandler по умолчанию.
 * Записывает аудит-события в стандартный вывод (System.out)
 * в формате: [AUDIT] <время> | action=<действие>.
 * Может быть заменен пользовательской реализацией через Spring DI.
 */
@Component
public class DefaultAuditHandler implements AuditHandler {

    /**
     * Обрабатывает событие аудита, выводя информацию в стандартный вывод.
     * @param action описание выполненного действия
     * @param args аргументы метода
     */
    @Override
    public void handle(String action, Object[] args) {
        System.out.println("[AUDIT] " + LocalDateTime.now() +
                " | action=" + action);
    }
}
