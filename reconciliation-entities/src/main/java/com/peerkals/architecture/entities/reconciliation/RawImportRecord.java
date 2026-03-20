package com.peerkals.architecture.entities.reconciliation;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "raw_import_record", schema = "reconciliation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawImportRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_file_id", nullable = false)
    private SourceFile sourceFile;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_file_sheet_id")
    private SourceFileSheet sourceFileSheet;
    @Column(name = "row_number", nullable = false)
    private Integer rowNumber;
    @Column(name = "source_row_key", length = 255)
    private String sourceRowKey;
    @Column(name = "raw_payload", nullable = false, columnDefinition = "jsonb")
    private String rawPayload;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parse_status_id")
    private Parameter parseStatus;
    @Column(name = "parse_error", columnDefinition = "TEXT")
    private String parseError;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "created_by", length = 100)
    private String createdBy;
}
