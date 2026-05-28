package com.conciliaciones.persistence.projection;

import java.time.LocalDateTime;

public interface ValidationExecutionDetailView {

    LocalDateTime getStartedAt();

    LocalDateTime getFinishedAt();

    Boolean getSuccessful();

    String getMessage();

    String getValidationDescription();

    String getStatusDescription();
}
