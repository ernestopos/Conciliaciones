package com.peerkals.architecture.entities.reconciliation;

import com.peerkals.architecture.entities.base.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "commission_payment_detail", schema = "reconciliation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommissionPaymentDetail extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "commission_payment_id", nullable = false)
    private CommissionPayment commissionPayment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id")
    private Policy policy;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commission_statement_item_id")
    private CommissionStatementItem commissionStatementItem;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reconciliation_case_id")
    private ReconciliationCase reconciliationCase;
    @Column(name = "concept", nullable = false, length = 255)
    private String concept;
    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;
    @Column(name = "included_for_payment", nullable = false)
    private Boolean includedForPayment;
    @Column(name = "exclusion_reason", length = 1000)
    private String exclusionReason;
}
