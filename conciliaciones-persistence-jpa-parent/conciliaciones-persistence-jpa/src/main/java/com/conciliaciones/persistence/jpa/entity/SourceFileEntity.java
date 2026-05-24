package com.conciliaciones.persistence.jpa.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(schema = "reconciliation", name = "source_file")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SourceFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="carrier_id",nullable=false)
    private Long carrierId;

    @Column(name="original_file_name",nullable=false,length=255)
    private String originalFileName;

    @Column(name="stored_file_name",length=255)
    private String storedFileName;

    @Column(name="file_extension",length=20)
    private String fileExtension;

    @Column(name="mime_type",length=100)
    private String mimeType;

    @Column(name="file_size_bytes")
    private Long fileSizeBytes;

    @Column(name="s3_bucket",length=255)
    private String s3Bucket;

    @Column(name="s3_key",length=500)
    private String s3Key;

    @Column(name="checksum_sha256",length=128)
    private String checksum;

    @Column(name="delimiter", nullable=false, length=5)
    private String delimiter = "|";

    @Column(name="detected_columns")
    private Integer detectedColumns;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name="detected_headers", columnDefinition="jsonb")
    private JsonNode detectedHeaders;

    @Column(name="source_system",length=100)
    private String sourceSystem;

    @Column(name="upload_date",nullable=false)
    private LocalDateTime uploadDate;

    @Column(name="uploaded_by",length=100)
    private String uploadedBy;

    @Column(name="processing_status_id",nullable=false)
    private Long processingStatusId;

    @Column(name="error_message",columnDefinition="TEXT")
    private String errorMessage;

    @Column(name="total_rows")
    private Integer totalRows;

    @Column(name="processed_rows")
    private Integer processedRows;

    @Column(name="failed_rows")
    private Integer failedRows;
}