package com.conciliaciones.msmanagementtask.infrastructure.scheduler.validation;

import org.springframework.stereotype.Component;

@Component
public class FileNotEmptyValidationTask implements IValidationFile {
    @Override
    public String validationTypeName() { return "FILE_NOT_EMPTY"; }

    @Override
    public ValidationExecutionResult executeValidation(ValidationTaskContext context) {
        if (context.rawRecords() != null && !context.rawRecords().isEmpty()) return ValidationExecutionResult.success();
        return ValidationExecutionResult.failed(java.util.List.of(ValidationError.general(
                "El archivo no tiene registros importados para validar.",
                "sourceFileId=" + context.sourceFile().getId()
        )));
    }
}
