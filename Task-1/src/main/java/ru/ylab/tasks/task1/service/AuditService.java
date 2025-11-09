package ru.ylab.tasks.task1.service;

import java.time.LocalDateTime;

public class AuditService {

    public void log(String message) {
        System.out.println("[" + LocalDateTime.now() + "] " + message);
    }
}
