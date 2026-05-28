package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.CommissionStatementItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommissionStatementItemRepository extends JpaRepository<CommissionStatementItemEntity, Long> {
}
