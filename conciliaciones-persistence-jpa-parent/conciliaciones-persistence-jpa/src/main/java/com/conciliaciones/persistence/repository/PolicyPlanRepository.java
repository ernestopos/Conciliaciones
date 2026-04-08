package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.PolicyPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyPlanRepository extends JpaRepository<PolicyPlanEntity, Long> {
}
