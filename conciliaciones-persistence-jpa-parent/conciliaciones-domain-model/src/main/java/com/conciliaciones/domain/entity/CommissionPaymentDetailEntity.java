package com.conciliaciones.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "reconciliation", name = "commission_payment_detail")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommissionPaymentDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="commission_payment_id",nullable=false)
    private Long commissionPaymentId;

    @Column(name="policy_id")
    private Long policyId;

    @Column(name="commission_statement_item_id")
    private Long commissionStatementItemId;

    @Column(name="reconciliation_case_id")
    private Long reconciliationCaseId;

    @Column(nullable=false,length=255)
    private String concept;

    @Column(nullable=false,precision=18,scale=2)
    private java.math.BigDecimal amount;

    @Column(name="included_for_payment",nullable=false)
    private Boolean includedForPayment;

    @Column(name="exclusion_reason",length=1000)
    private String exclusionReason;

    @Column(name="created_at",nullable=false)
    private java.time.LocalDateTime createdAt;

    @Column(name="created_by",length=100)
    private String createdBy;

    @Column(name="updated_at")
    private java.time.LocalDateTime updatedAt;

    @Column(name="updated_by",length=100)
    private String updatedBy;

}
