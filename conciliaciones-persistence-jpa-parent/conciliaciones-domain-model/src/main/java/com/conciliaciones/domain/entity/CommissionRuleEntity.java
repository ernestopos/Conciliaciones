package com.conciliaciones.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "reconciliation", name = "commission_rule")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommissionRuleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="carrier_id")
    private Long carrierId;

    @Column(name="role_id")
    private Long roleId;

    @Column(name="product_type",length=100)
    private String productType;

    @Column(length=100)
    private String state;

    @Column(name="rule_name",nullable=false,length=150)
    private String ruleName;

    @Column(name="rule_type_id",nullable=false)
    private Long ruleTypeId;

    @Column(precision=10,scale=4)
    private java.math.BigDecimal rate;

    @Column(name="fixed_amount",precision=18,scale=2)
    private java.math.BigDecimal fixedAmount;

    @Column(name="priority_order",nullable=false)
    private Integer priorityOrder;

    @Column(name="applies_from",nullable=false)
    private java.time.LocalDate appliesFrom;

    @Column(name="applies_to")
    private java.time.LocalDate appliesTo;

    @Column(nullable=false)
    private Boolean active;

    @Column(name="created_at",nullable=false)
    private java.time.LocalDateTime createdAt;

    @Column(name="created_by",length=100)
    private String createdBy;

    @Column(name="updated_at")
    private java.time.LocalDateTime updatedAt;

    @Column(name="updated_by",length=100)
    private String updatedBy;

}
