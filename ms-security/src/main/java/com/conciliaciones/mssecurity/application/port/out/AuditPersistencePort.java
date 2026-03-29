package com.conciliaciones.mssecurity.application.port.out;

import com.conciliaciones.mssecurity.infrastructure.adapter.out.persistence.AuditLogEntity;

public interface AuditPersistencePort {

    AuditLogEntity save(AuditLogEntity auditLogEntity);
}
