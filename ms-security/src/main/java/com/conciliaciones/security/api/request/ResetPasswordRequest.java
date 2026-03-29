package com.conciliaciones.security.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Solicitud para reiniciar contraseña")
public record ResetPasswordRequest(
        @NotBlank @Schema(example = "NuevoPassword123*") String newPassword,
        @Schema(example = "false") boolean temporary
) {
}
