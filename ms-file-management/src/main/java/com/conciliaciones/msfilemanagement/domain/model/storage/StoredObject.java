package com.conciliaciones.msfilemanagement.domain.model.storage;

import java.time.Instant;

public record StoredObject(
        String bucketName,
        String objectKey,
        Long size,
        Instant lastModified,
        String eTag
) {
}
