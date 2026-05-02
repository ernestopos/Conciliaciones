package com.conciliaciones.reconciliation.core.domain.model.storage;

import java.time.Instant;

public record StoredObject(
        String bucketName,
        String key,
        Long size,
        Instant lastModified,
        String eTag
) {
}
