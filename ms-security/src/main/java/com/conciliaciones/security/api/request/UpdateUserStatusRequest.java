package com.conciliaciones.security.api.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Solicitud para activar o inactivar usuario")
public record UpdateUserStatusRequest(
        @Schema(example = "false") boolean enabled
) {
}
