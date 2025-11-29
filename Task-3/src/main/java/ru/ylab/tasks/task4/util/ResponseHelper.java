package ru.ylab.tasks.task4.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.ylab.tasks.task4.dto.response.common.ErrorResponse;

import java.util.List;

@Component
public class ResponseHelper {

    public ResponseEntity<ErrorResponse> unauthorized(String code, String msg) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(code, List.of(msg)));
    }

    public ResponseEntity<ErrorResponse> forbidden(String code, String msg) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(code, List.of(msg)));
    }

    public ResponseEntity<ErrorResponse> badRequest(String code, String msg) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(code, List.of(msg)));
    }

    public ResponseEntity<ErrorResponse> serverError(String code, String msg) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(code, List.of(msg)));
    }

    public <T> ResponseEntity<T> created(T body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    public <T> ResponseEntity<T> ok(T body) {
        return ResponseEntity.ok(body);
    }
}
