package com.conciliaciones.msfilemanagement.infrastructure.adapter.out.persistence;

import com.conciliaciones.domain.audit.AuditInfo;
import com.conciliaciones.domain.file.FileType;
import com.conciliaciones.domain.file.SourceFile;
import com.conciliaciones.domain.processing.ProcessingStatus;
import com.conciliaciones.msfilemanagement.application.port.out.SourceFilePersistencePort;
import com.conciliaciones.msfilemanagement.infrastructure.exception.BusinessException;
import com.conciliaciones.persistence.jpa.entity.SourceFileEntity;
import com.conciliaciones.persistence.jpa.entity.SourceFileTraceabilityEntity;
import com.conciliaciones.persistence.repository.SourceFileRepository;
import com.conciliaciones.persistence.repository.SourceFileTraceabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SharedSourceFilePersistenceAdapter implements SourceFilePersistencePort {

    private static final String DEFAULT_SOURCE_SYSTEM = "MS_FILE_MANAGEMENT";
    private static final String DEFAULT_USER = "SYSTEM";

    private final SourceFileRepository sourceFileRepository;
    private final SourceFileTraceabilityRepository sourceFileTraceabilityRepository;

    @Override
    @Transactional
    public SourceFile save(SourceFile sourceFile) {
        SourceFileEntity entityToSave = toEntity(sourceFile);
        SourceFileEntity savedEntity = sourceFileRepository.save(entityToSave);
        saveTraceability(savedEntity, toProcessingStatus(savedEntity.getProcessingStatusId()), null, resolveCreatedBy(sourceFile));
        return toDomain(savedEntity);
    }

    @Override
    public Optional<SourceFile> findById(Long id) {
        return sourceFileRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<SourceFile> findByChecksum(String checksum) {
        return sourceFileRepository.findAll().stream()
                .filter(entity -> entity.getChecksum() != null)
                .filter(entity -> entity.getChecksum().equals(checksum))
                .findFirst()
                .map(this::toDomain);
    }

    @Override
    @Transactional
    public SourceFile updateStatus(Long sourceFileId, ProcessingStatus status, String errorMessage, String updatedBy) {
        SourceFileEntity entity = sourceFileRepository.findById(sourceFileId)
                .orElseThrow(() -> new BusinessException("No existe archivo fuente con id: " + sourceFileId));
        entity.setProcessingStatusId(status.getId());
        entity.setErrorMessage(errorMessage);
        SourceFileEntity savedEntity = sourceFileRepository.save(entity);
        saveTraceability(savedEntity, status, errorMessage, updatedBy);
        return toDomain(savedEntity);
    }

    private void saveTraceability(SourceFileEntity sourceFile, ProcessingStatus status, String errorMessage, String createdBy) {
        SourceFileTraceabilityEntity traceability = new SourceFileTraceabilityEntity();
        traceability.setSourceFile(sourceFile);
        traceability.setProcessingStatusId(status.getId());
        traceability.setStatusName(status.name());
        traceability.setDescription(status.getDescription());
        traceability.setErrorMessage(errorMessage);
        traceability.setCreatedAt(LocalDateTime.now());
        traceability.setCreatedBy(createdBy == null || createdBy.isBlank() ? DEFAULT_USER : createdBy);
        sourceFileTraceabilityRepository.save(traceability);
    }

    private SourceFileEntity toEntity(SourceFile sourceFile) {
        SourceFileEntity entity = new SourceFileEntity();
        entity.setId(sourceFile.getId());
        entity.setCarrierId(sourceFile.getClientId() == null ? 1L : sourceFile.getClientId());
        entity.setOriginalFileName(sourceFile.getOriginalFileName());
        entity.setStoredFileName(extractFileName(sourceFile.getStoragePath(), sourceFile.getOriginalFileName()));
        entity.setFileExtension(extractExtension(sourceFile.getOriginalFileName()));
        entity.setMimeType(sourceFile.getMimeType());
        entity.setFileSizeBytes(sourceFile.getFileSize());
        entity.setS3Bucket(extractBucket(sourceFile.getStoragePath()));
        entity.setS3Key(extractKey(sourceFile.getStoragePath()));
        entity.setChecksum(sourceFile.getChecksum());
        entity.setSourceSystem(DEFAULT_SOURCE_SYSTEM);
        entity.setUploadDate(resolveUploadDate(sourceFile));
        entity.setUploadedBy(resolveCreatedBy(sourceFile));
        entity.setProcessingStatusId(toProcessingStatusId(sourceFile.getProcessingStatus()));
        return entity;
    }

    private SourceFile toDomain(SourceFileEntity entity) {
        return SourceFile.builder()
                .id(entity.getId())
                .clientId(entity.getCarrierId())
                .originalFileName(entity.getOriginalFileName())
                .storagePath(buildStoragePath(entity.getS3Bucket(), entity.getS3Key()))
                .mimeType(entity.getMimeType())
                .fileSize(entity.getFileSizeBytes())
                .fileType(toFileType(entity.getFileExtension()))
                .checksum(entity.getChecksum())
                .processingStatus(toProcessingStatus(entity.getProcessingStatusId()))
                .bucketName(entity.getS3Bucket())
                .objectKey(entity.getS3Key())
                .auditInfo(AuditInfo.builder()
                        .createdBy(entity.getUploadedBy())
                        .createdAt(toOffsetDateTime(entity.getUploadDate()))
                        .build())
                .build();
    }

    private String extractFileName(String storagePath, String originalFileName) {
        if (storagePath == null || storagePath.isBlank()) {
            return originalFileName;
        }
        String normalized = storagePath.replace("\\", "/");
        int idx = normalized.lastIndexOf('/');
        return idx >= 0 ? normalized.substring(idx + 1) : normalized;
    }

    private String extractExtension(String fileName) {
        if (fileName == null || fileName.isBlank() || !fileName.contains(".")) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }

    private String extractBucket(String storagePath) {
        if (storagePath == null || storagePath.isBlank()) {
            return null;
        }
        String normalized = storagePath.replace("\\", "/");
        String withoutScheme = normalized.startsWith("s3://") ? normalized.substring(5) : normalized;
        int idx = withoutScheme.indexOf('/');
        return idx < 0 ? null : withoutScheme.substring(0, idx);
    }

    private String extractKey(String storagePath) {
        if (storagePath == null || storagePath.isBlank()) {
            return null;
        }
        String normalized = storagePath.replace("\\", "/");
        String withoutScheme = normalized.startsWith("s3://") ? normalized.substring(5) : normalized;
        int idx = withoutScheme.indexOf('/');
        return idx >= 0 ? withoutScheme.substring(idx + 1) : withoutScheme;
    }

    private String buildStoragePath(String bucket, String key) {
        if (bucket == null || bucket.isBlank()) {
            return key;
        }
        if (key == null || key.isBlank()) {
            return bucket;
        }
        return "s3://" + bucket + "/" + key;
    }

    private LocalDateTime resolveUploadDate(SourceFile sourceFile) {
        if (sourceFile.getAuditInfo() != null && sourceFile.getAuditInfo().getCreatedAt() != null) {
            return sourceFile.getAuditInfo().getCreatedAt().toLocalDateTime();
        }
        return LocalDateTime.now();
    }

    private String resolveCreatedBy(SourceFile sourceFile) {
        if (sourceFile.getAuditInfo() != null && sourceFile.getAuditInfo().getCreatedBy() != null && !sourceFile.getAuditInfo().getCreatedBy().isBlank()) {
            return sourceFile.getAuditInfo().getCreatedBy();
        }
        return DEFAULT_USER;
    }

    private Long toProcessingStatusId(ProcessingStatus status) {
        return status == null ? ProcessingStatus.PRESIGNED.getId() : status.getId();
    }

    private ProcessingStatus toProcessingStatus(Long statusId) {
        return ProcessingStatus.fromId(statusId);
    }

    private FileType toFileType(String extension) {
        if (extension == null || extension.isBlank()) {
            return null;
        }
        try {
            return FileType.valueOf(extension.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private OffsetDateTime toOffsetDateTime(LocalDateTime value) {
        return value == null ? null : value.atOffset(ZoneOffset.UTC);
    }
}
