package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.audit;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;

public record AuditLogResponse(
        Long id,
        String entityName,
        String entityId,
        Long actionId,
        String actionName,
        String username,
        LocalDateTime eventTimestamp,
        JsonNode oldValues,
        JsonNode newValues,
        String details
) {
}
