package com.conciliaciones.msmanagementtask.infrastructure.scheduler.tasks;

import com.conciliaciones.domain.entity.CommissionPaymentDetailEntity;
import com.conciliaciones.domain.entity.CommissionStatementEntity;
import com.conciliaciones.domain.entity.CommissionStatementItemEntity;
import com.conciliaciones.domain.entity.PolicyEntity;
import com.conciliaciones.persistence.jpa.entity.ScheduledTaskEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component("startPaymentProcessTask")
@Transactional
public class StartPaymentProcessTask extends AbstractManagementTask {

    private static final String SYSTEM_USER = "startPaymentProcessTask";
    private static final String STATUS_APPROVED = "Aprobada";
    private static final String PAYMENT_CONCEPT = "Pago comisión póliza aprobada";
    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final int DIVISION_SCALE = 10;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    protected void doExecute(ScheduledTaskEntity task) {
        Long sourceFileId = task.getExecutionPlanTask().getSourceFile().getId();

        log.info("LOG INICIO X = startPaymentProcessTask.calculatePayments - sourceFileId={}, executionPlanTaskId={}, taskId={}",
                sourceFileId, task.getExecutionPlanTask().getId(), task.getId());

        List<PaymentCandidate> candidates = findApprovedPoliciesPendingPayment(sourceFileId);

        int createdDetails = 0;
        int excludedDetails = 0;
        BigDecimal totalPayable = BigDecimal.ZERO;

        for (PaymentCandidate candidate : candidates) {
            CommissionPaymentDetailEntity detail = buildPaymentDetail(candidate);
            entityManager.persist(detail);

            if (Boolean.TRUE.equals(detail.getIncludedForPayment())) {
                createdDetails++;
                totalPayable = totalPayable.add(detail.getAmount());
            } else {
                excludedDetails++;
            }
        }

        log.info("LOG FIN X = startPaymentProcessTask.calculatePayments - sourceFileId={}, candidates={}, createdDetails={}, excludedDetails={}, totalPayable={}",
                sourceFileId, candidates.size(), createdDetails, excludedDetails, totalPayable);
    }

    private List<PaymentCandidate> findApprovedPoliciesPendingPayment(Long sourceFileId) {
        List<Object[]> rows = entityManager.createQuery("""
                        SELECT policy, statement, item
                          FROM CommissionStatementItemEntity item,
                               CommissionStatementEntity statement,
                               PolicyEntity policy
                         WHERE item.commissionStatementId = statement.id
                           AND statement.policyId = policy.id
                           AND statement.sourceFileId = :sourceFileId
                           AND policy.active = TRUE
                           AND (LOWER(policy.statusId.value) = LOWER(:approvedStatus) OR LOWER(policy.statusId.name) = LOWER(:approvedStatus))
                           AND NOT EXISTS (
                                SELECT 1
                                  FROM CommissionPaymentDetailEntity detail
                                 WHERE detail.policyId.id = policy.id
                                   AND detail.commissionStatementItemId.id = item.id
                           )
                         ORDER BY statement.producerId ASC, policy.id ASC, item.id ASC
                        """, Object[].class)
                .setParameter("sourceFileId", sourceFileId)
                .setParameter("approvedStatus", STATUS_APPROVED)
                .getResultList();

        return rows.stream()
                .map(row -> new PaymentCandidate(
                        (PolicyEntity) row[0],
                        (CommissionStatementEntity) row[1],
                        (CommissionStatementItemEntity) row[2]))
                .toList();
    }

    private CommissionPaymentDetailEntity buildPaymentDetail(PaymentCandidate candidate) {
        LocalDateTime now = LocalDateTime.now();
        CalculationResult calculationResult = calculatePaymentAmount(candidate.item());

        CommissionPaymentDetailEntity detail = new CommissionPaymentDetailEntity();
        detail.setPolicyId(candidate.policy());
        detail.setCommissionStatementItemId(candidate.item());
        detail.setConcept(buildConcept(candidate.statement(), candidate.item()));
        detail.setAmount(calculationResult.amount());
        detail.setIncludedForPayment(calculationResult.includedForPayment());
        detail.setExclusionReason(calculationResult.exclusionReason());
        detail.setCreatedAt(now);
        detail.setCreatedBy(SYSTEM_USER);
        return detail;
    }

    private CalculationResult calculatePaymentAmount(CommissionStatementItemEntity item) {
        if (item.getNetAmount() == null) {
            return excluded("No se puede calcular el pago porque net_amount viene vacío.");
        }
        if (item.getRate() == null) {
            return excluded("No se puede calcular el pago porque rate viene vacío.");
        }
        if (item.getCommissionRatePct() == null) {
            return excluded("No se puede calcular el pago porque commission_rate_pct viene vacío.");
        }

        BigDecimal percentRate = item.getRate().divide(HUNDRED, DIVISION_SCALE, RoundingMode.HALF_UP);
        BigDecimal percentCommission = item.getCommissionRatePct().divide(HUNDRED, DIVISION_SCALE, RoundingMode.HALF_UP);

        BigDecimal ratePayment = item.getNetAmount().multiply(percentRate);
        BigDecimal commissionPayment = ratePayment.multiply(percentCommission).setScale(2, RoundingMode.HALF_UP);

        return new CalculationResult(commissionPayment, Boolean.TRUE, null);
    }

    private CalculationResult excluded(String reason) {
        return new CalculationResult(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), Boolean.FALSE, reason);
    }

    private String buildConcept(CommissionStatementEntity statement, CommissionStatementItemEntity item) {
        return PAYMENT_CONCEPT
                + " | statementId=" + statement.getId()
                + " | itemId=" + item.getId()
                + " | fórmula=net_amount*(rate/100)*(commission_rate_pct/100)";
    }

    public record PaymentCandidate(
            PolicyEntity policy,
            CommissionStatementEntity statement,
            CommissionStatementItemEntity item
    ) {
    }

    private record CalculationResult(
            BigDecimal amount,
            Boolean includedForPayment,
            String exclusionReason
    ) {
    }
}