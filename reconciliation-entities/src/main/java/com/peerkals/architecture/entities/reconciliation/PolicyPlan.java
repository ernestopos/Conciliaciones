package com.peerkals.architecture.entities.reconciliation;

import com.peerkals.architecture.entities.base.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "policy_plan", schema = "reconciliation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyPlan extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;
    @Column(name = "plan_name", length = 200)
    private String planName;
    @Column(name = "base_medical_plan", length = 200)
    private String baseMedicalPlan;
    @Column(name = "coverage_plan_name", length = 200)
    private String coveragePlanName;
    @Column(name = "payment_type", length = 100)
    private String paymentType;
    @Column(name = "policy_mode", length = 100)
    private String policyMode;
}
