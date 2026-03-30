package com.conciliaciones.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "reconciliation", name = "policy_plan")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyPlanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="policy_id",nullable=false)
    private Long policyId;

    @Column(name="plan_name",length=200)
    private String planName;

    @Column(name="base_medical_plan",length=200)
    private String baseMedicalPlan;

    @Column(name="coverage_plan_name",length=200)
    private String coveragePlanName;

    @Column(name="payment_type",length=100)
    private String paymentType;

    @Column(name="policy_mode",length=100)
    private String policyMode;

    @Column(name="created_at",nullable=false)
    private java.time.LocalDateTime createdAt;

    @Column(name="created_by",length=100)
    private String createdBy;

    @Column(name="updated_at")
    private java.time.LocalDateTime updatedAt;

    @Column(name="updated_by",length=100)
    private String updatedBy;

}
