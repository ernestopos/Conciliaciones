package com.conciliaciones.persistence.repository.projection;

import java.time.LocalDateTime;

public interface SourceFileValidationDetailProjection {

    LocalDateTime getStartedAt();

    LocalDateTime getFinishedAt();

    Boolean getSuccessful();

    String getMessage();

    String getValidationTypeDescription();

    String getValidationStatusDescription();
}
