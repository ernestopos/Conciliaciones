package com.conciliaciones.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "reconciliation", name = "reconciliation_case")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliationCaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="source_file_id",nullable=false)
    private Long sourceFileId;

    @Column(name="commission_statement_id")
    private Long commissionStatementId;

    @Column(name="commission_statement_item_id")
    private Long commissionStatementItemId;

    @Column(name="carrier_id",nullable=false)
    private Long carrierId;

    @Column(name="policy_id")
    private Long policyId;

    @Column(name="producer_id")
    private Long producerId;

    @Column(name="case_type_id",nullable=false)
    private Long caseTypeId;

    @Column(name="severity_id",nullable=false)
    private Long severityId;

    @Column(name="status_id",nullable=false)
    private Long statusId;

    @Column(name="detected_at",nullable=false)
    private java.time.LocalDateTime detectedAt;

    @Column(name="description",nullable=false,columnDefinition="TEXT")
    private String description;

    @Column(name="suggested_action",length=1000)
    private String suggestedAction;

    @Column(name="resolution_notes",columnDefinition="TEXT")
    private String resolutionNotes;

    @Column(name="resolved_at")
    private java.time.LocalDateTime resolvedAt;

    @Column(name="resolved_by",length=100)
    private String resolvedBy;

    @Column(name="created_at",nullable=false)
    private java.time.LocalDateTime createdAt;

    @Column(name="created_by",length=100)
    private String createdBy;

    @Column(name="updated_at")
    private java.time.LocalDateTime updatedAt;

    @Column(name="updated_by",length=100)
    private String updatedBy;

}
