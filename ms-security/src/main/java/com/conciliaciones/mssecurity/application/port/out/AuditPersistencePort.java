package com.conciliaciones.mssecurity.application.port.out;

import com.conciliaciones.domain.entity.AuditLogEntity;

public interface AuditPersistencePort {

    AuditLogEntity save(AuditLogEntity auditLogEntity);
}
