package com.peerkals.architecture.entities.reconciliation;

import com.peerkals.architecture.entities.base.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "commission_payment", schema = "reconciliation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommissionPayment extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producer_id", nullable = false)
    private Producer producer;
    @Column(name = "period_year", nullable = false)
    private Integer periodYear;
    @Column(name = "period_month", nullable = false)
    private Integer periodMonth;
    @Column(name = "total_gross", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalGross;
    @Column(name = "total_adjustments", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalAdjustments;
    @Column(name = "total_payable", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalPayable;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "status_id", nullable = false)
    private Parameter status;
    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    @Column(name = "approved_by", length = 100)
    private String approvedBy;
    @Column(name = "paid_at")
    private LocalDateTime paidAt;
    @Column(name = "payment_reference", length = 100)
    private String paymentReference;
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
