package com.conciliaciones.mssecurity.application.usecase;

import com.conciliaciones.mssecurity.application.port.in.AuditUseCase;
import com.conciliaciones.mssecurity.application.port.out.AuditPersistencePort;
import com.conciliaciones.mssecurity.domain.model.AuditActionResult;
import com.conciliaciones.domain.entity.AuditLogEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService implements AuditUseCase {

    private final AuditPersistencePort auditPersistencePort;
    private final ObjectMapper objectMapper;

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
                                .actionId(1L)
                                .eventTimestamp(java.time.LocalDateTime.now())
                                .username(usuario)
                                .details(detalle + " | resultado=" + resultado.name() + " | estado=" + estado)
                                .oldValues(serializeSafe(valorAntes))
                .newValues(serializeSafe(valorDespues))
                .build();

        auditPersistencePort.save(auditLogEntity);
        log.info("LOG FIN X = register");
    }

    private String serializeSafe(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            log.error("Error serializando objeto de auditoría. objectType={}", object.getClass().getName(), ex);
            return "{\"serializationError\":true}";
        }
    }
}
