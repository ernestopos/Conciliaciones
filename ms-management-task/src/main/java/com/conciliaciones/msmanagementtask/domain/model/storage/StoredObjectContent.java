package com.conciliaciones.msmanagementtask.domain.model.storage;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public record StoredObjectContent(
        String bucketName,
        String objectKey,
        byte[] content,
        String contentType,
        Long contentLength,
        String eTag
) {

    public String asString() {
        return asString(StandardCharsets.UTF_8);
    }

    public String asString(Charset charset) {
        return new String(content, charset);
    }
}
