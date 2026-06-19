package com.conciliaciones.reconciliation.core.infrastructure.adapter.out.persistence.commissionReconciliation;

import com.conciliaciones.domain.entity.CommissionPaymentDetailEntity;
import com.conciliaciones.domain.entity.CommissionStatementItemEntity;
import com.conciliaciones.domain.entity.ParameterEntity;
import com.conciliaciones.domain.entity.PolicyEntity;
import com.conciliaciones.persistence.repository.CommissionPaymentDetailRepository;
import com.conciliaciones.persistence.repository.CommissionStatementItemRepository;
import com.conciliaciones.persistence.repository.ParameterRepository;
import com.conciliaciones.persistence.repository.PolicyRepository;
import com.conciliaciones.persistence.repository.projection.CommissionReconciliationView;
import com.conciliaciones.reconciliation.core.application.port.out.commissionReconciliation.CommissionReconciliationPersistencePort;
import com.conciliaciones.reconciliation.core.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommissionReconciliationPersistenceAdapter implements CommissionReconciliationPersistencePort {

    private static final String APPROVED_STATUS = "Aprobada";
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 100;

    private final CommissionStatementItemRepository commissionStatementItemRepository;
    private final PolicyRepository policyRepository;
    private final ParameterRepository parameterRepository;
    private final CommissionPaymentDetailRepository commissionPaymentDetailRepository;

    @Override
    public List<CommissionReconciliationView> findPending(
            String producerName,
            String policyNumber,
            String agencyName,
            String carrierName
    ) {
        return commissionStatementItemRepository.findPendingCommissionReconciliations(
                APPROVED_STATUS,
                normalize(producerName),
                normalize(policyNumber),
                normalize(agencyName),
                normalize(carrierName),
                PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE)
        );
    }

    @Override
    public Optional<CommissionReconciliationView> findByItemId(Long commissionStatementItemId) {
        return commissionStatementItemRepository.findCommissionReconciliationByItemId(commissionStatementItemId);
    }

    @Override
    public CommissionStatementItemEntity findItemById(Long id) {
        return commissionStatementItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el detalle de comisión con id: " + id));
    }

    @Override
    public PolicyEntity findPolicyById(Long id) {
        return policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la póliza con id: " + id));
    }

    @Override
    public ParameterEntity findParameterById(Long id) {
        return parameterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el parámetro con id: " + id));
    }

    @Override
    public CommissionStatementItemEntity saveItem(CommissionStatementItemEntity entity) {
        return commissionStatementItemRepository.save(entity);
    }

    @Override
    public PolicyEntity savePolicy(PolicyEntity entity) {
        return policyRepository.save(entity);
    }

    @Override
    public CommissionPaymentDetailEntity savePayment(CommissionPaymentDetailEntity entity) {
        return commissionPaymentDetailRepository.save(entity);
    }

    private String normalize(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }
}