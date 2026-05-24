package com.conciliaciones.msfilemanagement.application.usecase.storage;

import com.conciliaciones.domain.audit.AuditInfo;
import com.conciliaciones.domain.file.SourceFile;
import com.conciliaciones.domain.processing.ProcessingStatus;
import com.conciliaciones.msfilemanagement.application.port.in.storage.GeneratePresignedUploadUrlCommand;
import com.conciliaciones.msfilemanagement.application.port.in.storage.GeneratePresignedUploadUrlResult;
import com.conciliaciones.msfilemanagement.application.port.in.storage.GeneratePresignedUploadUrlUseCase;
import com.conciliaciones.msfilemanagement.application.port.out.SourceFilePersistencePort;
import com.conciliaciones.msfilemanagement.application.port.out.storage.ObjectStoragePort;
import com.conciliaciones.msfilemanagement.infrastructure.config.AwsS3Properties;
import com.conciliaciones.msfilemanagement.infrastructure.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class GeneratePresignedUploadUrlService implements GeneratePresignedUploadUrlUseCase {

    private static final String DEFAULT_USER = "SYSTEM";

    private final ObjectStoragePort objectStoragePort;
    private final BucketNameGenerator bucketNameGenerator;
    private final AwsS3Properties properties;
    private final SourceFilePersistencePort sourceFilePersistencePort;

    @Override
    public GeneratePresignedUploadUrlResult generate(GeneratePresignedUploadUrlCommand command) {
        if (command.fileName() == null || command.fileName().isBlank()) {
            throw new BusinessException("El nombre del archivo es obligatorio");
        }
        if (command.contentType() == null || command.contentType().isBlank()) {
            throw new BusinessException("El contentType es obligatorio");
        }
        if (command.carrierId() == null) {
            throw new BusinessException("El carrier es obligatorio para generar la URL prefirmada");
        }

        String bucketName = resolveBucket(command);
        if (!objectStoragePort.bucketExists(bucketName)) {
            objectStoragePort.createBucket(bucketName);
        }

        String objectKey = buildObjectKey(command.folder(), command.fileName());
        String presignedUrl = objectStoragePort.generatePresignedUploadUrl(bucketName, objectKey, command.contentType());

        SourceFile savedSourceFile = sourceFilePersistencePort.save(SourceFile.builder()
                .clientId(command.carrierId())
                .originalFileName(command.fileName().trim())
                .storagePath("s3://" + bucketName + "/" + objectKey)
                .mimeType(command.contentType())
                .processingStatus(ProcessingStatus.PRESIGNED)
                .auditInfo(AuditInfo.builder()
                        .createdBy(DEFAULT_USER)
                        .createdAt(OffsetDateTime.now())
                        .build())
                .build());

        return new GeneratePresignedUploadUrlResult(
                savedSourceFile.getId(),
                bucketName,
                objectKey,
                presignedUrl,
                properties.presignedUrlDurationMinutes()
        );
    }

    private String resolveBucket(GeneratePresignedUploadUrlCommand command) {
        if (command.createBucketPerUpload()) {
            return bucketNameGenerator.generate();
        }
        if (command.bucketName() != null && !command.bucketName().isBlank()) {
            return command.bucketName().toLowerCase();
        }
        return properties.defaultBucket();
    }

    private String buildObjectKey(String folder, String fileName) {
        String cleanFileName = fileName.trim().replace(" ", "-");
        if (folder == null || folder.isBlank()) {
            return cleanFileName;
        }
        String cleanFolder = folder.trim().replaceAll("^/+|/+$", "");
        return cleanFolder + "/" + cleanFileName;
    }
}
