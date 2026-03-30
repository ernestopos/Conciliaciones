package com.conciliaciones.persistence.jpa.entity;

import com.conciliaciones.domain.processing.ProcessingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "processing_execution")
public class ProcessingExecutionEntity extends BaseAuditEntity implements Auditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_file_id", nullable = false)
    private SourceFileEntity sourceFile;

    @Column(name = "execution_id", nullable = false, unique = true, length = 100)
    private String executionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ProcessingStatus status;

    @Column(name = "detail_message", length = 1000)
    private String detailMessage;
}
