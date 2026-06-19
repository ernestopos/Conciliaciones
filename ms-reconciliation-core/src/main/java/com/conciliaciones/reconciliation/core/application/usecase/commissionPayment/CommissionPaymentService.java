package com.conciliaciones.reconciliation.core.application.usecase.commissionPayment;

import com.conciliaciones.domain.entity.AgencyEntity;
import com.conciliaciones.domain.entity.CommissionStatementEntity;
import com.conciliaciones.domain.entity.PolicyEntity;
import com.conciliaciones.domain.entity.ProducerEntity;
import com.conciliaciones.persistence.repository.projection.CommissionPaymentDetailView;
import com.conciliaciones.reconciliation.core.application.port.in.commissionPayment.CreateManualCommissionPaymentUseCase;
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
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.commissionPayment.CreateManualCommissionPaymentRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.commissionPayment.RecalculateCommissionPaymentRequest;
import com.conciliaciones.reconciliation.core.infrastructure.exception.BusinessException;
import com.conciliaciones.reconciliation.core.infrastructure.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommissionPaymentService implements
        ListCommissionPaymentsUseCase,
        GetCommissionPaymentByIdUseCase,
        RecalculateCommissionPaymentUseCase,
        CreateManualCommissionPaymentUseCase {

    private final CommissionPaymentPersistencePort persistencePort;


    @Override
    @Transactional
    public CommissionPaymentResponse createManual(CreateManualCommissionPaymentRequest request, String username) {
        log.info("LOG INICIO X = createManualCommissionPayment policyId={} producerId={}", request.policyId(), request.producerId());

        PolicyEntity policy = persistencePort.findPolicyById(request.policyId())
                .orElseThrow(() -> new ResourceNotFoundException("Póliza no encontrada con id: " + request.policyId()));

        ProducerEntity producer = persistencePort.findProducerById(request.producerId())
                .orElseThrow(() -> new ResourceNotFoundException("Productor no encontrado con id: " + request.producerId()));

        AgencyEntity agency = null;
        if (request.agencyId() != null) {
            agency = persistencePort.findAgencyById(request.agencyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agencia no encontrada con id: " + request.agencyId()));
        }

        if (persistencePort.existsPaymentForPolicy(policy.getId())) {
            throw new BusinessException("La póliza ya tiene liquidación de comisión registrada: " + policy.getPolicyNumber());
        }

        LocalDateTime now = LocalDateTime.now();
        String createdBy = normalizeUsername(username);
        BigDecimal paymentAmount = calculatePaymentAmount(request.netAmount(), request.rate(), request.commissionRatePct());

        CommissionStatementEntity statement = CommissionStatementEntity.builder()
                .sourceFileId(null)
                .carrierId(policy.getCarrierId())
                .agencyId(agency != null ? agency.getId() : null)
                .producerId(producer.getId())
                .clientId(policy.getClientId())
                .policyId(policy.getId())
                .statementDate(request.statementDate() != null ? request.statementDate() : LocalDate.now())
                .paidDate(request.paidDate())
                .invoiceNumber(trimToNull(request.invoiceNumber()))
                .rowIdentifier("MANUAL-" + policy.getId() + "-" + System.currentTimeMillis())
                .sourceSheetName("MANUAL")
                .sourceRowNumber(1)
                .createdAt(now)
                .createdBy(createdBy)
                .build();

        CommissionStatementEntity savedStatement = persistencePort.saveStatement(statement);

        CommissionStatementItemEntity item = CommissionStatementItemEntity.builder()
                .commissionStatementId(savedStatement.getId())
                .netAmount(request.netAmount())
                .rate(request.rate())
                .commissionRatePct(request.commissionRatePct())
                .commissionAmount(paymentAmount)
                .createdAt(now)
                .createdBy(createdBy)
                .build();

        CommissionStatementItemEntity savedItem = persistencePort.saveItem(item);

        CommissionPaymentDetailEntity detail = CommissionPaymentDetailEntity.builder()
                .policyId(policy)
                .commissionStatementItemId(savedItem)
                .concept(trimToNull(request.concept()) != null ? request.concept().trim() : "LIQUIDACION_MANUAL")
                .amount(paymentAmount)
                .includedForPayment(request.includedForPayment() == null || Boolean.TRUE.equals(request.includedForPayment()))
                .createdAt(now)
                .createdBy(createdBy)
                .build();

        CommissionPaymentDetailEntity savedDetail = persistencePort.save(detail);

        log.info("LOG FIN X = createManualCommissionPayment detailId={} statementId={} itemId={} amount={}",
                savedDetail.getId(), savedStatement.getId(), savedItem.getId(), paymentAmount);

        return new CommissionPaymentResponse(
                savedDetail.getId(),
                policy.getId(),
                policy.getPolicyNumber(),
                savedStatement.getId(),
                producer.getId(),
                producer.getFullName(),
                agency != null ? agency.getId() : null,
                agency != null ? agency.getName() : null,
                policy.getCarrierId(),
                null,
                savedItem.getNetAmount(),
                savedItem.getRate(),
                savedItem.getCommissionRatePct(),
                savedDetail.getAmount(),
                savedDetail.getIncludedForPayment(),
                savedDetail.getCreatedAt()
        );
    }

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
        BigDecimal paymentAmount = calculatePaymentAmount(request.netAmount(), request.rate(), request.commissionRatePct());
        item.setNetAmount(request.netAmount());
        item.setRate(request.rate());
        item.setCommissionRatePct(request.commissionRatePct());
        item.setCommissionAmount(paymentAmount);
        item.setUpdatedAt(LocalDateTime.now());
        item.setUpdatedBy("ADMIN");
        entity.setAmount(paymentAmount);
        entity.setIncludedForPayment(Boolean.TRUE.equals(request.includedForPayment()));
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
    private BigDecimal calculatePaymentAmount(BigDecimal netAmount, BigDecimal rate, BigDecimal commissionRatePct) {
        return netAmount
                .multiply(rate)
                .multiply(commissionRatePct)
                .divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP);
    }

    private String normalizeUsername(String username) {
        return trimToNull(username) != null ? username.trim() : "ADMIN";
    }

    private String trimToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

}