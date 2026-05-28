package com.conciliaciones.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "reconciliation", name = "commission_statement_item")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommissionStatementItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="commission_statement_id",nullable=false)
    private Long commissionStatementId;

    @Column(name="gross_premium",precision=18,scale=2)
    private java.math.BigDecimal grossPremium;

    @Column(name="commissionable_premium",precision=18,scale=2)
    private java.math.BigDecimal commissionablePremium;

    @Column(precision=18,scale=2)
    private java.math.BigDecimal premium;

    @Column(name="net_amount",precision=18,scale=2)
    private java.math.BigDecimal netAmount;

    @Column(precision=10,scale=4)
    private java.math.BigDecimal rate;

    @Column(name="commission_rate_pct",precision=7,scale=4)
    private java.math.BigDecimal commissionRatePct;

    @Column(name="commission_amount",precision=18,scale=2)
    private java.math.BigDecimal commissionAmount;

    @Column(precision=18,scale=2)
    private java.math.BigDecimal subtotal;

    @Column(name="month_amount",precision=18,scale=2)
    private java.math.BigDecimal monthAmount;

    @Column
    private Integer applicants;

    @Column(name="contract_count")
    private Integer contractCount;

    @Column(name="retro_count")
    private Integer retroCount;

    @Column(name="vip_count")
    private Integer vipCount;

    @Column(name="is_initial_premium")
    private Boolean isInitialPremium;

    @Column(name="created_at",nullable=false)
    private java.time.LocalDateTime createdAt;

    @Column(name="created_by",length=100)
    private String createdBy;

    @Column(name="updated_at")
    private java.time.LocalDateTime updatedAt;

    @Column(name="updated_by",length=100)
    private String updatedBy;

}
