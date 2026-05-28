package com.conciliaciones.msmanagementtask.infrastructure.scheduler.validation;

import com.conciliaciones.domain.entity.RawImportRecordEntity;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class EmptyRowValidationTask implements IValidationFile {
    @Override
    public String validationTypeName() { return "EMPTY_ROW"; }

    @Override
    public ValidationExecutionResult executeValidation(ValidationTaskContext context) {
        List<ValidationError> errors = new ArrayList<>();
        for (RawImportRecordEntity raw : context.rawRecords()) {
            if (isEmptyPayload(raw.getRawPayload())) {
                errors.add(ValidationError.row(raw, null, null, "La fila está vacía."));
            }
        }
        return errors.isEmpty() ? ValidationExecutionResult.success() : ValidationExecutionResult.failed(errors);
    }

    private boolean isEmptyPayload(JsonNode payload) {
        if (payload == null || payload.isNull() || payload.isEmpty()) return true;
        Iterator<Map.Entry<String, JsonNode>> fields = payload.fields();
        while (fields.hasNext()) {
            JsonNode value = fields.next().getValue();
            if (value != null && !value.isNull() && !value.asText("").isBlank()) return false;
        }
        return true;
    }
}
