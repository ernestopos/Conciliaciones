package com.conciliaciones.mssecurity.infrastructure.adapter.out.persistence;

import com.conciliaciones.mssecurity.application.port.out.AuditPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditPersistenceAdapter implements AuditPersistencePort {

    private final AuditLogRepository auditLogRepository;

    @Override
    public AuditLogEntity save(AuditLogEntity auditLogEntity) {
        log.info("LOG INICIO X = save");
        AuditLogEntity saved = auditLogRepository.save(auditLogEntity);
        log.info("LOG FIN X = save");
        return saved;
    }
}
