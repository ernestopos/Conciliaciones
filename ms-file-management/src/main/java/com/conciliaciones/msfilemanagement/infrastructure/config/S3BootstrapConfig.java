package com.conciliaciones.msfilemanagement.infrastructure.config;

import com.conciliaciones.msfilemanagement.application.port.out.storage.ObjectStoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.aws.s3", name = "enabled", havingValue = "true", matchIfMissing = true)
public class S3BootstrapConfig {

    private final AwsS3Properties properties;
    private final ObjectStoragePort objectStoragePort;

    @EventListener(ApplicationReadyEvent.class)
    public void ensureDefaultBucketExists() {
        if (!properties.autoCreateDefaultBucket()) {
            log.info("Bootstrap de bucket por defecto deshabilitado.");
            return;
        }

        String defaultBucket = properties.defaultBucket();
        if (defaultBucket == null || defaultBucket.isBlank()) {
            log.warn("No se configuró app.aws.s3.default-bucket. Se omite bootstrap del bucket.");
            return;
        }

        if (!objectStoragePort.bucketExists(defaultBucket)) {
            log.info("Creando bucket por defecto: {}", defaultBucket);
            objectStoragePort.createBucket(defaultBucket);
            return;
        }

        log.info("Bucket por defecto ya existe: {}", defaultBucket);
    }
}
