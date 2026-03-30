package com.conciliaciones.mssecurity.infrastructure.adapter.out.persistence;

import com.conciliaciones.domain.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {
}
