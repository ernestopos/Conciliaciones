package com.conciliaciones.msmanagementtask.infrastructure.scheduler.validation;

import org.springframework.stereotype.Component;

@Component
public class FileExtensionValidationTask implements IValidationFile {

    @Override
    public String validationTypeName() { return "FILE_EXTENSION"; }

    @Override
    public ValidationExecutionResult executeValidation(ValidationTaskContext context) {
        String extension = context.sourceFile().getFileExtension();
        String fileName = context.sourceFile().getOriginalFileName();
        boolean validByExtension = extension != null && extension.replace(".", "").equalsIgnoreCase("csv");
        boolean validByName = fileName != null && fileName.toLowerCase().endsWith(".csv");
        if (validByExtension || validByName) return ValidationExecutionResult.success();
        return ValidationExecutionResult.failed(java.util.List.of(ValidationError.general(
                "La extensión del archivo no es válida. Se esperaba CSV.",
                "fileExtension=" + extension + ", originalFileName=" + fileName
        )));
    }
}