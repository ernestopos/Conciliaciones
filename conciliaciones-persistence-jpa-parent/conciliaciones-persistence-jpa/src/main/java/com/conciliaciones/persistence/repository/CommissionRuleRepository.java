package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.CommissionRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommissionRuleRepository extends JpaRepository<CommissionRuleEntity, Long> {
}
