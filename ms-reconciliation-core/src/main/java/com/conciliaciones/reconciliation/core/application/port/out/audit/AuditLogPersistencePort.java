package com.conciliaciones.reconciliation.core.application.port.out.audit;

import com.conciliaciones.domain.entity.AuditLogEntity;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogPersistencePort {
    Optional<AuditLogEntity> findById(Long id);
    Page<AuditLogEntity> findAll(String entityName, String entityId, Long actionId, String username,
                                 LocalDateTime from, LocalDateTime to, Pageable pageable);
}
