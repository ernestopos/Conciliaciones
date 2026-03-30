package com.conciliaciones.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "reconciliation", name = "policy_status_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyStatusHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="policy_id",nullable=false)
    private Long policyId;

    @Column(name="source_file_id")
    private Long sourceFileId;

    @Column(name="status_id",nullable=false)
    private Long statusId;

    @Column(name="effective_from",nullable=false)
    private java.time.LocalDate effectiveFrom;

    @Column(name="effective_to")
    private java.time.LocalDate effectiveTo;

    @Column(length=1000)
    private String notes;

    @Column(name="created_at",nullable=false)
    private java.time.LocalDateTime createdAt;

    @Column(name="created_by",length=100)
    private String createdBy;

}
