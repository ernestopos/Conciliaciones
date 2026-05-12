package com.conciliaciones.mssecurity.application.usecase;

import com.conciliaciones.mssecurity.application.port.in.AuditUseCase;
import com.conciliaciones.mssecurity.domain.model.AuditActionResult;
import com.conciliaciones.domain.entity.AuditLogEntity;
import com.conciliaciones.persistence.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService implements AuditUseCase {

    private static final Long AUDIT_ACTION_INSERT_ID = 57L;
    private static final Long AUDIT_ACTION_PROCESS_ID = 60L;
    private static final Long AUDIT_ACTION_LOGIN_ID = 63L;

    private static final String ACTION_LOGIN = "LOGIN";
    private static final String ACTION_VALIDATE_TOKEN = "VALIDATE_TOKEN";
    private static final String ACTION_VALIDATE_ROLES = "VALIDATE_ROLES";

    private final ObjectMapper objectMapper;
    private final AuditLogRepository auditLogRepository;

    @Override
    public void register(String usuario,
                         String accion,
                         AuditActionResult resultado,
                         String detalle,
                         Object valorAntes,
                         Object valorDespues,
                         String estado) {
        log.info("LOG INICIO X = register");

        AuditLogEntity auditLogEntity = AuditLogEntity.builder()
                .entityName("SECURITY")
                .entityId(usuario == null ? "NA" : usuario)
                .actionId(resolveActionId(accion))
                .eventTimestamp(java.time.LocalDateTime.now())
                .username(usuario)
                .details(detalle + " | resultado=" + resultado.name() + " | estado=" + estado)
                .oldValues(toJsonNodeSafe(valorAntes))
                .newValues(toJsonNodeSafe(valorDespues))
                .build();

        auditLogRepository.save(auditLogEntity);
        log.info("LOG FIN X = register");
    }

    private Long resolveActionId(String accion) {
        if (accion == null || accion.isBlank()) {
            return AUDIT_ACTION_PROCESS_ID;
        }

        return switch (accion.trim().toUpperCase()) {
            case ACTION_LOGIN -> AUDIT_ACTION_LOGIN_ID;
            case ACTION_VALIDATE_TOKEN, ACTION_VALIDATE_ROLES -> AUDIT_ACTION_PROCESS_ID;
            default -> AUDIT_ACTION_INSERT_ID;
        };
    }

    private JsonNode toJsonNodeSafe(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return objectMapper.valueToTree(object);
        } catch (Exception ex) {
            log.error("Error serializando objeto de auditoría. objectType={}", object.getClass().getName(), ex);
            return objectMapper.createObjectNode().put("serializationError", true);
        }
    }
}