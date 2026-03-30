package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.PolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyRepository extends JpaRepository<PolicyEntity, Long> {
}
