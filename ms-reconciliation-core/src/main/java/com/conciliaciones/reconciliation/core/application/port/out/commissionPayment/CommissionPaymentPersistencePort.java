package com.conciliaciones.reconciliation.core.application.port.out.commissionPayment;

import com.conciliaciones.persistence.repository.projection.CommissionPaymentDetailView;
import com.conciliaciones.domain.entity.AgencyEntity;
import com.conciliaciones.domain.entity.CommissionPaymentDetailEntity;
import com.conciliaciones.domain.entity.CommissionStatementEntity;
import com.conciliaciones.domain.entity.CommissionStatementItemEntity;
import com.conciliaciones.domain.entity.PolicyEntity;
import com.conciliaciones.domain.entity.ProducerEntity;
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
    CommissionStatementEntity saveStatement(CommissionStatementEntity entity);
    CommissionStatementItemEntity saveItem(CommissionStatementItemEntity entity);
    Optional<PolicyEntity> findPolicyById(Long id);
    Optional<ProducerEntity> findProducerById(Long id);
    Optional<AgencyEntity> findAgencyById(Long id);
    boolean existsPaymentForPolicy(Long policyId);
}