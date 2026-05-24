package com.conciliaciones.reconciliation.core.application.port.in.audit;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.audit.AuditLogResponse;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListAuditLogsUseCase {
    Page<AuditLogResponse> list(String entityName, String entityId, Long actionId, String username,
                                LocalDateTime from, LocalDateTime to, Pageable pageable);
}
