package com.conciliaciones.reconciliation.core.infrastructure.config;

import com.conciliaciones.reconciliation.core.infrastructure.exception.BusinessException;
import com.conciliaciones.reconciliation.core.infrastructure.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(ResourceNotFoundException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "message", ex.getMessage(),
                "status", HttpStatus.NOT_FOUND.value()
        );
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleBusiness(BusinessException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "message", ex.getMessage(),
                "status", HttpStatus.BAD_REQUEST.value()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "message", "Error de validación",
                "status", HttpStatus.BAD_REQUEST.value()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleGeneric(Exception ex) {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "message", ex.getMessage(),
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
    }
}
