package com.peerkals.architecture.entities.reconciliation;

import com.peerkals.architecture.entities.base.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reconciliation_case", schema = "reconciliation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReconciliationCase extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_file_id", nullable = false)
    private SourceFile sourceFile;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commission_statement_id")
    private CommissionStatement commissionStatement;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commission_statement_item_id")
    private CommissionStatementItem commissionStatementItem;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrier_id", nullable = false)
    private Carrier carrier;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id")
    private Policy policy;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producer_id")
    private Producer producer;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_type_id", nullable = false)
    private Parameter caseType;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "severity_id", nullable = false)
    private Parameter severity;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "status_id", nullable = false)
    private Parameter status;
    @Column(name = "detected_at", nullable = false)
    private LocalDateTime detectedAt;
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(name = "suggested_action", length = 1000)
    private String suggestedAction;
    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    @Column(name = "resolved_by", length = 100)
    private String resolvedBy;
}
