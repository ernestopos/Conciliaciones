package com.conciliaciones.msmanagementtask.infrastructure.scheduler.validation;

import com.conciliaciones.domain.entity.ParameterEntity;
import com.conciliaciones.domain.entity.RawImportRecordEntity;
import com.conciliaciones.persistence.repository.ParameterRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RequiredFieldValidationTask implements IValidationFile {

    private static final String VALIDATION_TYPE_NAME = "REQUIRED_FIELD";
    private static final String REQUIRED_SUFFIX = "_1";

    private final ParameterRepository parameterRepository;

    @Override
    public String validationTypeName() {
        return VALIDATION_TYPE_NAME;
    }

    @Override
    public ValidationExecutionResult executeValidation(ValidationTaskContext context) {
        List<ValidationError> errors = new ArrayList<>();

        List<ParameterEntity> requiredFields = parameterRepository
                .findByParameterGroupAndActiveTrueOrderBySortOrderAsc(ValidationConstants.VALIDATION_HEADER_STRUCTURE)
                .stream()
                .filter(this::isRequired)
                .toList();

        if (requiredFields.isEmpty()) {
            return ValidationExecutionResult.success();
        }

        for (RawImportRecordEntity raw : context.rawRecords()) {
            JsonNode payload = raw.getRawPayload();
            if (payload == null || !payload.isObject()) {
                continue;
            }

            for (ParameterEntity requiredField : requiredFields) {
                String fieldName = requiredField.getName();
                JsonNode value = payload.get(fieldName);

                if (value == null || value.isNull() || value.asText("").isBlank()) {
                    errors.add(ValidationError.row(
                            raw,
                            fieldName,
                            value == null || value.isNull() ? null : value.asText(),
                            "Campo obligatorio sin valor."
                    ));
                }
            }
        }

        return errors.isEmpty()
                ? ValidationExecutionResult.success()
                : ValidationExecutionResult.failed(errors);
    }

    private boolean isRequired(ParameterEntity parameter) {
        return parameter.getValue() != null && parameter.getValue().trim().endsWith(REQUIRED_SUFFIX);
    }
}
