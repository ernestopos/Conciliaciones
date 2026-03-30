package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.PolicyStatusHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyStatusHistoryRepository extends JpaRepository<PolicyStatusHistoryEntity, Long> {
}
