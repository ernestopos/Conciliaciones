package com.conciliaciones.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(schema = "reconciliation", name = "raw_import_record")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawImportRecordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="source_file_id",nullable=false)
    private Long sourceFileId;

    @Column(name="source_file_sheet_id")
    private Long sourceFileSheetId;

    @Column(name="row_number",nullable=false)
    private Integer rowNumber;

    @Column(name="source_row_key",length=255)
    private String sourceRowKey;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_payload", columnDefinition = "jsonb", nullable = false)
    private JsonNode rawPayload;

    @Column(name="parse_status_id")
    private Long parseStatusId;

    @Column(name="parse_error",columnDefinition="TEXT")
    private String parseError;

    @Column(name="created_at",nullable=false)
    private java.time.LocalDateTime createdAt;

    @Column(name="created_by",length=100)
    private String createdBy;

}
