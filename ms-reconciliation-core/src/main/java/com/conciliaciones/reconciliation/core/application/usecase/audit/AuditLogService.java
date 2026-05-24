package com.conciliaciones.reconciliation.core.application.usecase.audit;

import com.conciliaciones.domain.entity.AuditLogEntity;
import com.conciliaciones.domain.entity.ParameterEntity;
import com.conciliaciones.reconciliation.core.application.port.in.audit.GetAuditLogByIdUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.audit.ListAuditLogsUseCase;
import com.conciliaciones.reconciliation.core.application.port.out.audit.AuditLogPersistencePort;
import com.conciliaciones.reconciliation.core.application.port.out.parameter.ParameterPersistencePort;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.audit.AuditLogResponse;
import com.conciliaciones.reconciliation.core.infrastructure.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuditLogService implements ListAuditLogsUseCase, GetAuditLogByIdUseCase {

    private final AuditLogPersistencePort persistencePort;
    private final ParameterPersistencePort parameterPersistencePort;

    @Override
    public Page<AuditLogResponse> list(String entityName, String entityId, Long actionId, String username,
                                       LocalDateTime from, LocalDateTime to, Pageable pageable) {
        log.info("LOG INICIO X = listAuditLogs page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<AuditLogResponse> result = persistencePort.findAll(entityName, entityId, actionId, username, from, to, pageable)
                .map(this::toResponse);
        log.info("LOG FIN X = listAuditLogs totalElements={}", result.getTotalElements());
        return result;
    }

    @Override
    public AuditLogResponse getById(Long id) {
        log.info("LOG INICIO X = getAuditLogById id={}", id);
        AuditLogEntity entity = persistencePort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de auditoría no encontrado con id: " + id));
        log.info("LOG FIN X = getAuditLogById id={}", entity.getId());
        return toResponse(entity);
    }

    private AuditLogResponse toResponse(AuditLogEntity entity) {
        String actionName = parameterPersistencePort.findById(entity.getActionId())
                .map(ParameterEntity::getName)
                .orElse(null);

        return new AuditLogResponse(
                entity.getId(),
                entity.getEntityName(),
                entity.getEntityId(),
                entity.getActionId(),
                actionName,
                entity.getUsername(),
                entity.getEventTimestamp(),
                entity.getOldValues(),
                entity.getNewValues(),
                entity.getDetails()
        );
    }
}
