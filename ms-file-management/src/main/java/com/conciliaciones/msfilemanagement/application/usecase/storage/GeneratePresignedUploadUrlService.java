package com.conciliaciones.msfilemanagement.application.usecase.storage;

import com.conciliaciones.msfilemanagement.application.port.in.storage.GeneratePresignedUploadUrlCommand;
import com.conciliaciones.msfilemanagement.application.port.in.storage.GeneratePresignedUploadUrlResult;
import com.conciliaciones.msfilemanagement.application.port.in.storage.GeneratePresignedUploadUrlUseCase;
import com.conciliaciones.msfilemanagement.application.port.out.storage.ObjectStoragePort;
import com.conciliaciones.msfilemanagement.infrastructure.config.AwsS3Properties;
import com.conciliaciones.msfilemanagement.infrastructure.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeneratePresignedUploadUrlService implements GeneratePresignedUploadUrlUseCase {

    private final ObjectStoragePort objectStoragePort;
    private final BucketNameGenerator bucketNameGenerator;
    private final AwsS3Properties properties;

    @Override
    public GeneratePresignedUploadUrlResult generate(GeneratePresignedUploadUrlCommand command) {
        if (command.fileName() == null || command.fileName().isBlank()) {
            throw new BusinessException("El nombre del archivo es obligatorio");
        }
        if (command.contentType() == null || command.contentType().isBlank()) {
            throw new BusinessException("El contentType es obligatorio");
        }

        String bucketName = resolveBucket(command);
        if (!objectStoragePort.bucketExists(bucketName)) {
            objectStoragePort.createBucket(bucketName);
        }

        String objectKey = buildObjectKey(command.folder(), command.fileName());
        String presignedUrl = objectStoragePort.generatePresignedUploadUrl(bucketName, objectKey, command.contentType());

        return new GeneratePresignedUploadUrlResult(
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
