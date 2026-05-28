package com.conciliaciones.msmanagementtask.domain.model.storage;

import java.time.Instant;

public record StoredObject(
        String bucketName,
        String objectKey,
        Long size,
        Instant lastModified,
        String eTag
) {
}
