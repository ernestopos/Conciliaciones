package com.conciliaciones.persistence.jpa.entity;

import com.conciliaciones.domain.entity.ParameterEntity;
import com.conciliaciones.domain.entity.RawImportRecordEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(schema = "reconciliation", name = "source_file_validation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SourceFileValidationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "validation_source_plan_id", nullable = false)
    private ValidationSourcePlanEntity validationSourcePlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_import_record_id")
    private RawImportRecordEntity rawImportRecordEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "validation_type_id", nullable = false)
    private ParameterEntity validationTypeId;

    @Column(name = "validation_status_id", nullable = false)
    private Long validationStatusId;

    @Column(name = "row_number")
    private Integer rowNumber;

    @Column(name = "column_name", length = 150)
    private String columnName;

    @Column(name = "field_value", columnDefinition = "TEXT")
    private String fieldValue;

    @Column(name = "message", nullable = false, length = 2000)
    private String message;

    @Column(name = "technical_detail", columnDefinition = "TEXT")
    private String technicalDetail;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;
    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
