package ru.ylab.tasks.task4.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.ylab.tasks.task4.dto.response.common.ErrorResponse;

import java.util.List;

/**
 * Утилитарный класс для формирования стандартных HTTP-ответов.
 * Предоставляет методы для создания ResponseEntity с различными статусами
 * и стандартизированной структурой ошибок.
 */
@Component
public class ResponseHelper {

    /**
     * Создает ответ со статусом 401 Unauthorized.
     * @param code код ошибки
     * @param msg сообщение об ошибке
     * @return ResponseEntity с ошибкой авторизации
     */
    public ResponseEntity<ErrorResponse> unauthorized(String code, String msg) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(code, List.of(msg)));
    }

    /**
     * Создает ответ со статусом 403 Forbidden.
     * @param code код ошибки
     * @param msg сообщение об ошибке
     * @return ResponseEntity с ошибкой доступа
     */
    public ResponseEntity<ErrorResponse> forbidden(String code, String msg) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(code, List.of(msg)));
    }

    /**
     * Создает ответ со статусом 400 Bad Request.
     * @param code код ошибки
     * @param msg сообщение об ошибке
     * @return ResponseEntity с ошибкой валидации
     */
    public ResponseEntity<ErrorResponse> badRequest(String code, String msg) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(code, List.of(msg)));
    }

    /**
     * Создает ответ со статусом 500 Internal Server Error.
     * @param code код ошибки
     * @param msg сообщение об ошибке
     * @return ResponseEntity с ошибкой сервера
     */
    public ResponseEntity<ErrorResponse> serverError(String code, String msg) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(code, List.of(msg)));
    }

    /**
     * Создает ответ со статусом 201 Created.
     * @param <T> тип тела ответа
     * @param body тело ответа
     * @return ResponseEntity со статусом Created
     */
    public <T> ResponseEntity<T> created(T body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    /**
     * Создает ответ со статусом 200 OK.
     * @param <T> тип тела ответа
     * @param body тело ответа
     * @return ResponseEntity со статусом OK
     */
    public <T> ResponseEntity<T> ok(T body) {
        return ResponseEntity.ok(body);
    }
}
