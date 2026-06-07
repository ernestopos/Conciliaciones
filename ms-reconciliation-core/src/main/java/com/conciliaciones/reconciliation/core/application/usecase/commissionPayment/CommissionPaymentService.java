package com.conciliaciones.reconciliation.core.application.usecase.commissionPayment;

import com.conciliaciones.persistence.repository.projection.CommissionPaymentDetailView;
import com.conciliaciones.reconciliation.core.application.port.in.commissionPayment.ListCommissionPaymentsUseCase;
import com.conciliaciones.reconciliation.core.application.port.out.commissionPayment.CommissionPaymentPersistencePort;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.commissionPayment.CommissionPaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.conciliaciones.domain.entity.CommissionPaymentDetailEntity;
import com.conciliaciones.domain.entity.CommissionStatementItemEntity;
import com.conciliaciones.reconciliation.core.application.port.in.commissionPayment.GetCommissionPaymentByIdUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.commissionPayment.RecalculateCommissionPaymentUseCase;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.commissionPayment.RecalculateCommissionPaymentRequest;
import com.conciliaciones.reconciliation.core.infrastructure.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommissionPaymentService implements
        ListCommissionPaymentsUseCase,
        GetCommissionPaymentByIdUseCase,
        RecalculateCommissionPaymentUseCase {

    private final CommissionPaymentPersistencePort persistencePort;

    @Override
    public List<CommissionPaymentResponse> list(
            String producerName,
            String policyNumber,
            String agencyName,
            String carrierName
    ) {
        log.info("LOG INICIO X = listCommissionPayments producerName={} policyNumber={} agencyName={} carrierName={}",
                producerName, policyNumber, agencyName, carrierName);

        List<CommissionPaymentResponse> response = persistencePort
                .findCommissionPayments(producerName, policyNumber, agencyName, carrierName)
                .stream()
                .map(this::toResponse)
                .toList();

        log.info("LOG FIN X = listCommissionPayments total={}", response.size());
        return response;
    }

    private CommissionPaymentResponse toResponse(CommissionPaymentDetailView view) {
        return new CommissionPaymentResponse(
                view.getId(),
                view.getPolicyId(),
                view.getPolicyName(),
                view.getCommissionStatementId(),
                view.getProducerId(),
                view.getProducerName(),
                view.getAgencyId(),
                view.getAgencyName(),
                view.getCarrierId(),
                view.getCarrierName(),
                view.getNetAmount(),
                view.getRate(),
                view.getCommissionRatePct(),
                view.getPaymentAmount(),
                view.getIncludedForPayment(),
                view.getCreatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CommissionPaymentResponse getById(Long id) {
        log.info("LOG INICIO X = getCommissionPaymentById id={}", id);

        CommissionPaymentDetailEntity entity = persistencePort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago de comisión no encontrado con id: " + id));

        log.info("LOG FIN X = getCommissionPaymentById id={}", entity.getId());
        return toResponse(entity);
    }

    @Override
    @Transactional
    public CommissionPaymentResponse recalculate(Long id, RecalculateCommissionPaymentRequest request) {
        log.info("LOG INICIO X = recalculateCommissionPayment id={}", id);
        CommissionPaymentDetailEntity entity = persistencePort.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pago de comisión no encontrado con id: " + id));
        CommissionStatementItemEntity item = entity.getCommissionStatementItemId();
        BigDecimal paymentAmount = request.netAmount().multiply(request.rate()).multiply(request.commissionRatePct()).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP);
        item.setNetAmount(request.netAmount());
        item.setRate(request.rate());
        item.setCommissionRatePct(request.commissionRatePct());
        item.setCommissionAmount(paymentAmount);
        item.setUpdatedAt(LocalDateTime.now());
        item.setUpdatedBy("ADMIN");
        entity.setAmount(paymentAmount);
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy("ADMIN");
        CommissionPaymentDetailEntity saved = persistencePort.save(entity);
        log.info("LOG FIN X = recalculateCommissionPayment id={} paymentAmount={}", saved.getId(), paymentAmount);
        return toResponse(saved);
    }

    private CommissionPaymentResponse toResponse(CommissionPaymentDetailEntity entity) {
        CommissionStatementItemEntity item = entity.getCommissionStatementItemId();

        return new CommissionPaymentResponse(
                entity.getId(),
                entity.getPolicyId() != null ? entity.getPolicyId().getId() : null,
                entity.getPolicyId() != null ? entity.getPolicyId().getPolicyNumber() : null,
                item != null ? item.getCommissionStatementId() : null,
                null,
                null,
                null,
                null,
                null,
                null,
                item != null ? item.getNetAmount() : null,
                item != null ? item.getRate() : null,
                item != null ? item.getCommissionRatePct() : null,
                entity.getAmount(),
                entity.getIncludedForPayment(),
                entity.getCreatedAt()
        );
    }
}