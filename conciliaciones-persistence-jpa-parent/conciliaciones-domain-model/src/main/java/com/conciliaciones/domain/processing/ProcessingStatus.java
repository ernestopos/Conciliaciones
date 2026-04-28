package com.conciliaciones.domain.processing;

import java.util.Arrays;

public enum ProcessingStatus {
    PRESIGNED(1L, "PRESIGNED"),
    S3_UPLOAD(2L, "S3_UPLOAD"),
    DATA_REVIEW(3L, "DATA-REVIEW"),
    PROCESS_DATA(4L, "PROCESS-DATA"),
    RULER_APLICATION(5L, "RULER-APLICATION"),
    PROCESS_FINALICE(6L, "PROCESS-FINALICE"),
    ERROR_PRESIGNED(7L, "ERROR_PRESIGNED"),
    ERROR_S3_UPLOAD(8L, "ERROR_S3_UPLOAD"),
    ERROR_DATA_REVIEW(9L, "ERROR_DATA-REVIEW"),
    ERROR_PROCESS_DATA(10L, "ERROR_PROCESS-DATA"),
    ERROR_RULER_APLICATION(11L, "ERROR_RULER-APLICATION");

    private final Long id;
    private final String description;

    ProcessingStatus(Long id, String description) {
        this.id = id;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public static ProcessingStatus fromId(Long id) {
        if (id == null) {
            return PRESIGNED;
        }
        return Arrays.stream(values())
                .filter(status -> status.id.equals(id))
                .findFirst()
                .orElse(PRESIGNED);
    }
}
