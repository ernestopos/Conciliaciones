package com.conciliaciones.security.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta estándar de la API")
public record ApiResponse<T>(
        @Schema(example = "true") boolean success,
        @Schema(example = "Operación exitosa") String message,
        T data
) {
    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }
}
