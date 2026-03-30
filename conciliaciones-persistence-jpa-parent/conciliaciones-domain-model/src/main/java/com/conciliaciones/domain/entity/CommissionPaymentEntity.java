package com.conciliaciones.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "reconciliation", name = "commission_payment")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommissionPaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="producer_id",nullable=false)
    private Long producerId;

    @Column(name="period_year",nullable=false)
    private Integer periodYear;

    @Column(name="period_month",nullable=false)
    private Integer periodMonth;

    @Column(name="total_gross",nullable=false,precision=18,scale=2)
    private java.math.BigDecimal totalGross;

    @Column(name="total_adjustments",nullable=false,precision=18,scale=2)
    private java.math.BigDecimal totalAdjustments;

    @Column(name="total_payable",nullable=false,precision=18,scale=2)
    private java.math.BigDecimal totalPayable;

    @Column(name="status_id",nullable=false)
    private Long statusId;

    @Column(name="generated_at",nullable=false)
    private java.time.LocalDateTime generatedAt;

    @Column(name="approved_at")
    private java.time.LocalDateTime approvedAt;

    @Column(name="approved_by",length=100)
    private String approvedBy;

    @Column(name="paid_at")
    private java.time.LocalDateTime paidAt;

    @Column(name="payment_reference",length=100)
    private String paymentReference;

    @Column(columnDefinition="TEXT")
    private String notes;

    @Column(name="created_at",nullable=false)
    private java.time.LocalDateTime createdAt;

    @Column(name="created_by",length=100)
    private String createdBy;

    @Column(name="updated_at")
    private java.time.LocalDateTime updatedAt;

    @Column(name="updated_by",length=100)
    private String updatedBy;

}
