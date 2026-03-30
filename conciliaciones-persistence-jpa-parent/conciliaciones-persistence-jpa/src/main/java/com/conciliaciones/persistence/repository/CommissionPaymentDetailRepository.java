package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.CommissionPaymentDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommissionPaymentDetailRepository extends JpaRepository<CommissionPaymentDetailEntity, Long> {
}
