package com.conciliaciones.security.domain.model;

import java.time.OffsetDateTime;

public record AuditEvent(
        String eventId,
        String eventType,
        String username,
        String action,
        String status,
        String detail,
        OffsetDateTime createdAt
) {
}
