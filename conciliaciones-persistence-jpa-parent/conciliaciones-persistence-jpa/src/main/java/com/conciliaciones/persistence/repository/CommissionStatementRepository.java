package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.CommissionStatementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommissionStatementRepository extends JpaRepository<CommissionStatementEntity, Long> {
}
