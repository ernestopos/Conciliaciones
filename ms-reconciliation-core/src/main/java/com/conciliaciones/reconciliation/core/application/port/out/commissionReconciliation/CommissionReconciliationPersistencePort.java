package com.conciliaciones.reconciliation.core.application.port.out.commissionReconciliation;

import com.conciliaciones.domain.entity.CommissionPaymentDetailEntity;
import com.conciliaciones.domain.entity.CommissionStatementItemEntity;
import com.conciliaciones.domain.entity.ParameterEntity;
import com.conciliaciones.domain.entity.PolicyEntity;
import com.conciliaciones.persistence.repository.projection.CommissionReconciliationView;

import java.util.List;
import java.util.Optional;

public interface CommissionReconciliationPersistencePort {

    List<CommissionReconciliationView> findPending(
            String producerName,
            String policyNumber,
            String agencyName,
            String carrierName
    );

    Optional<CommissionReconciliationView> findByItemId(Long commissionStatementItemId);

    CommissionStatementItemEntity findItemById(Long id);

    PolicyEntity findPolicyById(Long id);

    ParameterEntity findParameterById(Long id);

    CommissionStatementItemEntity saveItem(CommissionStatementItemEntity entity);

    PolicyEntity savePolicy(PolicyEntity entity);

    CommissionPaymentDetailEntity savePayment(CommissionPaymentDetailEntity entity);
}