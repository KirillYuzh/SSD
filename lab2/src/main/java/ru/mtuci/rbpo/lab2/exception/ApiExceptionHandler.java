package ru.mtuci.rbpo.lab2.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.mtuci.rbpo.lab2.api.ApiError;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Преобразует исключения сервисного слоя в единый формат тела ошибки.
 * Методы-обработчики описаны аннотациями OpenAPI, поэтому ответы 400, 404 и 409
 * попадают в спецификацию каждого эндпоинта.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ApiResponse(responseCode = "404", description = "Ресурс не найден",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<ApiError> handleNotFound(NotFoundException exception, HttpServletRequest request) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler(ConflictException.class)
    @ApiResponse(responseCode = "409", description = "Операция нарушает состояние данных",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<ApiError> handleConflict(ConflictException exception, HttpServletRequest request) {
        return response(HttpStatus.CONFLICT, exception.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ApiResponse(responseCode = "400", description = "Поля тела запроса не прошли проверку",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception,
                                                     HttpServletRequest request) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return response(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ApiResponse(responseCode = "400", description = "Тело запроса не соответствует формату ресурса",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<ApiError> handleUnreadableBody(HttpMessageNotReadableException exception,
                                                         HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST,
                "Тело запроса не соответствует формату ресурса: проверьте имена полей и значения перечислений",
                request);
    }

    private ResponseEntity<ApiError> response(HttpStatus status, String message, HttpServletRequest request) {
        ApiError body = new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message,
                request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
