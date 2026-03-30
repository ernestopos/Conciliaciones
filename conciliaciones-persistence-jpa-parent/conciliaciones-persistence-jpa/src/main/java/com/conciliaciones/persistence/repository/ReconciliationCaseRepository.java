package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.ReconciliationCaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReconciliationCaseRepository extends JpaRepository<ReconciliationCaseEntity, Long> {
}
