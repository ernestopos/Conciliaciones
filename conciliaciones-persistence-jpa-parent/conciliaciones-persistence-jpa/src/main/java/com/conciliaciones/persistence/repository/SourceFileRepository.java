package com.conciliaciones.persistence.repository;

import com.conciliaciones.persistence.jpa.entity.SourceFileEntity;
import com.conciliaciones.persistence.repository.projection.DonutCharValTolProjection;
import com.conciliaciones.persistence.repository.projection.SourceFileProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SourceFileRepository extends JpaRepository<SourceFileEntity, Long> {

    @Query(
            value = """
            SELECT
                sf.id AS id,
                sf.carrier_id AS carrierId,
                sf.original_file_name AS originalFileName,
                sf.stored_file_name AS storedFileName,
                sf.file_extension AS fileExtension,
                sf.mime_type AS mimeType,
                sf.file_size_bytes AS fileSizeBytes,
                sf.s3_bucket AS s3Bucket,
                sf.s3_key AS s3Key,
                sf.checksum_sha256 AS checksum,
                sf.source_system AS sourceSystem,
                sf.upload_date AS uploadDate,
                sf.uploaded_by AS uploadedBy,
                sf.processing_status_id AS processingStatusId,
                p.value AS processingStatusName,
                sf.error_message AS errorMessage,
                sf.total_rows AS totalRows,
                sf.processed_rows AS processedRows,
                sf.failed_rows AS failedRows
            FROM reconciliation.source_file sf
            INNER JOIN reconciliation.parameter p
                ON p.id = sf.processing_status_id
            WHERE p.parameter_group = 'SOURCE_FILE_STATUS'
            ORDER BY sf.upload_date DESC
            """,
            countQuery = """
            SELECT COUNT(sf.id)
            FROM reconciliation.source_file sf
            INNER JOIN reconciliation.parameter p
                ON p.id = sf.processing_status_id
            WHERE p.parameter_group = 'SOURCE_FILE_STATUS'
            """,
            nativeQuery = true
    )
    Page<SourceFileProjection> findAllWithProcessingStatus(Pageable pageable);

    @Query(value = """
        select p.value as statusName, count(p.value) as total
        from reconciliation.source_file sf
        inner join reconciliation.parameter p on p.id = sf.processing_status_id
        group by p.value
    """, nativeQuery = true)
    List<DonutCharValTolProjection> charFileUploads();
}