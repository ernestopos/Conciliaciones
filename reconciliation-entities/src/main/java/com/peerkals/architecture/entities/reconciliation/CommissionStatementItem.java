package com.peerkals.architecture.entities.reconciliation;

import com.peerkals.architecture.entities.base.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "commission_statement_item", schema = "reconciliation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommissionStatementItem extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "commission_statement_id", nullable = false)
    private CommissionStatement commissionStatement;
    @Column(name = "gross_premium", precision = 18, scale = 2)
    private BigDecimal grossPremium;
    @Column(name = "commissionable_premium", precision = 18, scale = 2)
    private BigDecimal commissionablePremium;
    @Column(name = "premium", precision = 18, scale = 2)
    private BigDecimal premium;
    @Column(name = "net_amount", precision = 18, scale = 2)
    private BigDecimal netAmount;
    @Column(name = "rate", precision = 10, scale = 4)
    private BigDecimal rate;
    @Column(name = "commission_rate_pct", precision = 7, scale = 4)
    private BigDecimal commissionRatePct;
    @Column(name = "commission_amount", precision = 18, scale = 2)
    private BigDecimal commissionAmount;
    @Column(name = "subtotal", precision = 18, scale = 2)
    private BigDecimal subtotal;
    @Column(name = "month_amount", precision = 18, scale = 2)
    private BigDecimal monthAmount;
    @Column(name = "applicants")
    private Integer applicants;
    @Column(name = "contract_count")
    private Integer contractCount;
    @Column(name = "retro_count")
    private Integer retroCount;
    @Column(name = "vip_count")
    private Integer vipCount;
    @Column(name = "is_initial_premium")
    private Boolean isInitialPremium;
}
