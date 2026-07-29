package com.conciliaciones.persistence.repository.projection;

import java.time.LocalDateTime;

public interface SourceFileProjection {

    Long getId();

    Long getCarrierId();

    String getOriginalFileName();

    String getStoredFileName();

    String getFileExtension();

    String getMimeType();

    Long getFileSizeBytes();

    String getS3Bucket();

    String getS3Key();

    String getChecksum();

    String getSourceSystem();

    LocalDateTime getUploadDate();

    String getUploadedBy();

    Long getProcessingStatusId();

    String getProcessingStatusName();

    String getErrorMessage();

    Integer getTotalRows();

    Integer getProcessedRows();

    Integer getFailedRows();
}