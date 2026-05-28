package com.conciliaciones.msmanagementtask.infrastructure.scheduler.validation;

public interface IValidationFile {

    String validationTypeName();

    ValidationExecutionResult executeValidation(ValidationTaskContext context);
}
