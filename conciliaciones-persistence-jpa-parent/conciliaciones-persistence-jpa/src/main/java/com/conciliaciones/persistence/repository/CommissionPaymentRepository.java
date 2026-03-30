package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.CommissionPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommissionPaymentRepository extends JpaRepository<CommissionPaymentEntity, Long> {
}
