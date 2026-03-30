package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.CommissionAssignmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommissionAssignmentRepository extends JpaRepository<CommissionAssignmentEntity, Long> {
}
