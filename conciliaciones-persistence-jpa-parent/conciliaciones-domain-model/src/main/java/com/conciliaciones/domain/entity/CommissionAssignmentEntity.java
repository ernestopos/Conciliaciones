package com.conciliaciones.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "reconciliation", name = "commission_assignment")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommissionAssignmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="policy_id",nullable=false)
    private Long policyId;

    @Column(name="producer_id",nullable=false)
    private Long producerId;

    @Column(name="role_id",nullable=false)
    private Long roleId;

    @Column(name="split_percentage",precision=7,scale=4)
    private java.math.BigDecimal splitPercentage;

    @Column(name="valid_from",nullable=false)
    private java.time.LocalDate validFrom;

    @Column(name="valid_to")
    private java.time.LocalDate validTo;

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
