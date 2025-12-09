package ru.ylab.tasks.task5.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO для ответа на успешный выход из системы.
 * Содержит сообщение о результате выхода.
 */
@Data
@AllArgsConstructor
public class LogoutResponse {

    private String message;

}
