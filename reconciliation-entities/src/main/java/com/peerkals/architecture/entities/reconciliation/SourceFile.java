package com.peerkals.architecture.entities.reconciliation;

import com.peerkals.architecture.entities.base.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "source_file", schema = "reconciliation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SourceFile extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrier_id", nullable = false)
    private Carrier carrier;
    @Column(name = "original_file_name", nullable = false, length = 255)
    private String originalFileName;
    @Column(name = "stored_file_name", length = 255)
    private String storedFileName;
    @Column(name = "file_extension", length = 20)
    private String fileExtension;
    @Column(name = "mime_type", length = 100)
    private String mimeType;
    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;
    @Column(name = "s3_bucket", length = 255)
    private String s3Bucket;
    @Column(name = "s3_key", length = 500)
    private String s3Key;
    @Column(name = "checksum_sha256", length = 128)
    private String checksumSha256;
    @Column(name = "source_system", length = 100)
    private String sourceSystem;
    @Column(name = "upload_date", nullable = false)
    private LocalDateTime uploadDate;
    @Column(name = "uploaded_by", length = 100)
    private String uploadedBy;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "processing_status_id", nullable = false)
    private Parameter processingStatus;
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    @Column(name = "total_rows")
    private Integer totalRows;
    @Column(name = "processed_rows")
    private Integer processedRows;
    @Column(name = "failed_rows")
    private Integer failedRows;
}
