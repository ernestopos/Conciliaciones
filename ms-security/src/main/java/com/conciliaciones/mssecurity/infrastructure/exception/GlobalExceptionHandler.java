package com.conciliaciones.mssecurity.infrastructure.exception;

import com.conciliaciones.mssecurity.domain.exception.CarrierPortalNotFoundException;
import com.conciliaciones.mssecurity.domain.exception.SecureBrowserLaunchException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        log.info("LOG INICIO X = handleAuthenticationException");
        log.error("Error autenticación: {}", ex.getMessage(), ex);
        ResponseEntity<ApiErrorResponse> response = ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiErrorResponse(
                        "AUTH_ERROR",
                        ex.getMessage(),
                        List.of(),
                        OffsetDateTime.now()
                ));
        log.info("LOG FIN X = handleAuthenticationException");
        return response;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        log.info("LOG INICIO X = handleValidationException");
        List<String> details = ex.getBindingResult().getAllErrors().stream()
                .map(error -> error instanceof FieldError fieldError
                        ? fieldError.getField() + ": " + fieldError.getDefaultMessage()
                        : error.getDefaultMessage())
                .toList();

        ResponseEntity<ApiErrorResponse> response = ResponseEntity.badRequest()
                .body(new ApiErrorResponse(
                        "VALIDATION_ERROR",
                        "La solicitud contiene errores de validación",
                        details,
                        OffsetDateTime.now()));
        log.info("LOG FIN X = handleValidationException");
        return response;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {
        log.info("LOG INICIO X = handleGenericException");
        log.error("Error no controlado", ex);
        ResponseEntity<ApiErrorResponse> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse(
                        "INTERNAL_ERROR",
                        "Se presentó un error interno",
                        List.of(),
                        OffsetDateTime.now()));
        log.info("LOG FIN X = handleGenericException");
        return response;
    }

    @ExceptionHandler(CarrierPortalNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleCarrierPortalNotFound(
            CarrierPortalNotFoundException exception,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                exception.getMessage(),
                List.of(),
                OffsetDateTime.now());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(SecureBrowserLaunchException.class)
    public ResponseEntity<ApiErrorResponse> handleSecureBrowserLaunch(
            SecureBrowserLaunchException exception,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(),
                exception.getMessage(),
                List.of(),
                OffsetDateTime.now());

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }


}
