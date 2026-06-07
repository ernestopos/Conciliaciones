package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.PolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PolicyRepository extends JpaRepository<PolicyEntity, Long>, JpaSpecificationExecutor<PolicyEntity> {
}