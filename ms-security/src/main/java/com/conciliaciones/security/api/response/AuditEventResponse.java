package com.conciliaciones.security.api.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "Evento de auditoría")
public record AuditEventResponse(
        String eventId,
        String eventType,
        String username,
        String action,
        String status,
        String detail,
        OffsetDateTime createdAt
) {
}
