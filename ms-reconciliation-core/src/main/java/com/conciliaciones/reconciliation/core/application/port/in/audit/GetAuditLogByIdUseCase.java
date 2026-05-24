package com.conciliaciones.reconciliation.core.application.port.in.audit;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.audit.AuditLogResponse;

public interface GetAuditLogByIdUseCase {
    AuditLogResponse getById(Long id);
}
