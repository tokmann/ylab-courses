package ru.ylab.tasks.task5.audit.handler;

import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class DefaultAuditHandler implements AuditHandler {

    @Override
    public void handle(String action, Object[] args) {
        System.out.println("[AUDIT] " + LocalDateTime.now() +
                " | action=" + action);
    }
}
