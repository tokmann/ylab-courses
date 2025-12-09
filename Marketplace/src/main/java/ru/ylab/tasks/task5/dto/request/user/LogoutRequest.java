package ru.ylab.tasks.task5.dto.request.user;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для запроса на выход из системы.
 */
@Data
@NoArgsConstructor
public class LogoutRequest {

    private String confirm;

}
