package com.conciliaciones.reconciliation.core.application.port.out.commissionPayment;

import com.conciliaciones.persistence.repository.projection.CommissionPaymentDetailView;
import com.conciliaciones.domain.entity.CommissionPaymentDetailEntity;
import java.util.Optional;

import java.util.List;

public interface CommissionPaymentPersistencePort {

    List<CommissionPaymentDetailView> findCommissionPayments(
            String producerName,
            String policyNumber,
            String agencyName,
            String carrierName
    );
    Optional<CommissionPaymentDetailEntity> findById(Long id);
    CommissionPaymentDetailEntity save(CommissionPaymentDetailEntity entity);
}