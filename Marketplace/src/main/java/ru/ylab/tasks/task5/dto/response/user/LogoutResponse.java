package ru.ylab.tasks.task5.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для ответа на успешный выход из системы.
 * Содержит сообщение о результате выхода.
 */
@Data
@AllArgsConstructor
public class LogoutResponse {

    private String message;

}
