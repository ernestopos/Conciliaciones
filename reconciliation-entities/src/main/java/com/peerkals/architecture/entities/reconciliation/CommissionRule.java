package com.peerkals.architecture.entities.reconciliation;

import com.peerkals.architecture.entities.base.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "commission_rule", schema = "reconciliation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommissionRule extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrier_id")
    private Carrier carrier;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Parameter role;
    @Column(name = "product_type", length = 100)
    private String productType;
    @Column(name = "state", length = 100)
    private String state;
    @Column(name = "rule_name", nullable = false, length = 150)
    private String ruleName;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rule_type_id", nullable = false)
    private Parameter ruleType;
    @Column(name = "rate", precision = 10, scale = 4)
    private BigDecimal rate;
    @Column(name = "fixed_amount", precision = 18, scale = 2)
    private BigDecimal fixedAmount;
    @Column(name = "priority_order", nullable = false)
    private Integer priorityOrder;
    @Column(name = "applies_from", nullable = false)
    private LocalDate appliesFrom;
    @Column(name = "applies_to")
    private LocalDate appliesTo;
    @Column(name = "active", nullable = false)
    private Boolean active;
}
