package com.conciliaciones.msmanagementtask.infrastructure.scheduler.validation;

import java.util.List;

public record ValidationExecutionResult(List<ValidationError> errors) {

    public static ValidationExecutionResult success() {
        return new ValidationExecutionResult(List.of());
    }

    public static ValidationExecutionResult failed(List<ValidationError> errors) {
        return new ValidationExecutionResult(errors == null ? List.of() : errors);
    }

    public boolean successful() {
        return errors == null || errors.isEmpty();
    }
}
