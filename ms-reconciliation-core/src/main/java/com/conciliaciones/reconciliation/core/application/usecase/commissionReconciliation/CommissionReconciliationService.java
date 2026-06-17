package com.conciliaciones.reconciliation.core.application.usecase.commissionReconciliation;

import com.conciliaciones.domain.entity.CommissionPaymentDetailEntity;
import com.conciliaciones.domain.entity.CommissionStatementItemEntity;
import com.conciliaciones.domain.entity.ParameterEntity;
import com.conciliaciones.domain.entity.PolicyEntity;
import com.conciliaciones.persistence.repository.projection.CommissionReconciliationView;
import com.conciliaciones.reconciliation.core.application.port.in.commissionReconciliation.GenerateCommissionPaymentUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.commissionReconciliation.GetCommissionReconciliationByIdUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.commissionReconciliation.ListCommissionReconciliationsUseCase;
import com.conciliaciones.reconciliation.core.application.port.out.commissionReconciliation.CommissionReconciliationPersistencePort;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.commissionReconciliation.CommissionReconciliationResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.commissionReconciliation.GenerateCommissionPaymentRequest;
import com.conciliaciones.reconciliation.core.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import com.conciliaciones.reconciliation.core.infrastructure.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class CommissionReconciliationService implements
        ListCommissionReconciliationsUseCase,
        GetCommissionReconciliationByIdUseCase,
        GenerateCommissionPaymentUseCase {

    private static final String APPROVED_STATUS = "Aprobada";
    private static final String SYSTEM_USER = "ADMIN";
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private final CommissionReconciliationPersistencePort persistencePort;

    @Override
    @Transactional(readOnly = true)
    public List<CommissionReconciliationResponse> list(
            String producerName,
            String policyNumber,
            String agencyName,
            String carrierName
    ) {
        return persistencePort.findPending(producerName, policyNumber, agencyName, carrierName)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CommissionReconciliationResponse getByCommissionStatementItemId(Long commissionStatementItemId) {
        CommissionReconciliationView view = persistencePort.findByItemId(commissionStatementItemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe conciliación para el detalle de comisión con id: " + commissionStatementItemId
                ));

        return toResponse(view);
    }

    @Override
    @Transactional
    public CommissionReconciliationResponse generatePayment(
            Long commissionStatementItemId,
            GenerateCommissionPaymentRequest request
    ) {
        CommissionReconciliationView currentView = persistencePort.findByItemId(commissionStatementItemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe conciliación para el detalle de comisión con id: " + commissionStatementItemId
                ));

        CommissionStatementItemEntity item = persistencePort.findItemById(commissionStatementItemId);
        PolicyEntity policy = persistencePort.findPolicyById(currentView.getPolicyId());

        updatePolicyStatusIfRequired(policy, request);
        updateCommissionValuesIfRequired(item, request);

        validatePolicyApproved(policy);
        validateCommissionValues(item);

        persistencePort.savePolicy(policy);
        persistencePort.saveItem(item);

        BigDecimal paymentAmount = calculatePaymentAmount(item);

        CommissionPaymentDetailEntity paymentDetail = buildPaymentDetail(policy, item, paymentAmount);
        persistencePort.savePayment(paymentDetail);

        CommissionReconciliationView updatedView = persistencePort.findByItemId(commissionStatementItemId)
                .orElse(currentView);

        return toResponse(updatedView);
    }

    private void updatePolicyStatusIfRequired(
            PolicyEntity policy,
            GenerateCommissionPaymentRequest request
    ) {
        if (request.getPolicyStatusId() == null) {
            return;
        }

        ParameterEntity status = persistencePort.findParameterById(request.getPolicyStatusId());
        policy.setStatusId(status);
    }

    private void updateCommissionValuesIfRequired(
            CommissionStatementItemEntity item,
            GenerateCommissionPaymentRequest request
    ) {
        if (request.getNetAmount() != null) {
            item.setNetAmount(request.getNetAmount());
        }

        if (request.getRate() != null) {
            item.setRate(request.getRate());
        }

        if (request.getCommissionRatePct() != null) {
            item.setCommissionRatePct(request.getCommissionRatePct());
        }
    }

    private void validatePolicyApproved(PolicyEntity policy) {
        if (policy.getStatusId() == null) {
            throw new IllegalArgumentException("La póliza no tiene estado asignado.");
        }

        String statusName = policy.getStatusId().getName();
        String statusValue = policy.getStatusId().getValue();

        boolean approvedByName = APPROVED_STATUS.equalsIgnoreCase(statusName);
        boolean approvedByValue = APPROVED_STATUS.equalsIgnoreCase(statusValue);

        if (!approvedByName && !approvedByValue) {
            throw new BusinessException("La póliza debe quedar en estado Aprobada para generar el pago.");
        }
    }

    private void validateCommissionValues(CommissionStatementItemEntity item) {
        if (item.getNetAmount() == null) {
            throw new BusinessException("El valor netAmount es obligatorio.");
        }

        if (item.getNetAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("El valor netAmount no puede ser menor a cero.");
        }

        if (item.getRate() == null) {
            throw new BusinessException("El valor rate es obligatorio.");
        }

        if (item.getRate().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El valor rate debe ser mayor a cero.");
        }

        if (item.getCommissionRatePct() == null) {
            throw new BusinessException("El valor commissionRatePct es obligatorio.");
        }

        if (item.getCommissionRatePct().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El valor commissionRatePct debe ser mayor a cero.");
        }
    }

    private BigDecimal calculatePaymentAmount(CommissionStatementItemEntity item) {
        return item.getNetAmount()
                .multiply(item.getRate().divide(HUNDRED, 10, RoundingMode.HALF_UP))
                .multiply(item.getCommissionRatePct().divide(HUNDRED, 10, RoundingMode.HALF_UP))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private CommissionPaymentDetailEntity buildPaymentDetail(
            PolicyEntity policy,
            CommissionStatementItemEntity item,
            BigDecimal paymentAmount
    ) {
        CommissionPaymentDetailEntity entity = new CommissionPaymentDetailEntity();

        entity.setPolicyId(policy);
        entity.setCommissionStatementItemId(item);
        entity.setConcept("Pago comisión generado desde conciliación");
        entity.setAmount(paymentAmount);
        entity.setIncludedForPayment(Boolean.TRUE);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setCreatedBy(SYSTEM_USER);

        return entity;
    }

    private CommissionReconciliationResponse toResponse(CommissionReconciliationView view) {
        return CommissionReconciliationResponse.builder()
                .commissionStatementItemId(view.getCommissionStatementItemId())
                .commissionStatementId(view.getCommissionStatementId())

                .policyId(view.getPolicyId())
                .policyNumber(view.getPolicyNumber())

                .policyStatusId(view.getPolicyStatusId())
                .policyStatusName(view.getPolicyStatusName())

                .clientId(view.getClientId())
                .clientName(view.getClientName())

                .producerId(view.getProducerId())
                .producerName(view.getProducerName())

                .agencyId(view.getAgencyId())
                .agencyName(view.getAgencyName())

                .carrierId(view.getCarrierId())
                .carrierName(view.getCarrierName())

                .netAmount(view.getNetAmount())
                .rate(view.getRate())
                .commissionRatePct(view.getCommissionRatePct())
                .estimatedPaymentAmount(view.getEstimatedPaymentAmount())

                .reconciliationType(view.getReconciliationType())
                .reconciliationReason(view.getReconciliationReason())

                .createdAt(view.getCreatedAt())
                .build();
    }
}