package com.conciliaciones.msmanagementtask.infrastructure.scheduler.validation;

import com.conciliaciones.domain.entity.RawImportRecordEntity;
import com.conciliaciones.persistence.repository.ProducerRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ValidateProducerValidationTask implements IValidationFile {

    private static final String VALIDATION_TYPE_NAME = "VALIDATE_PRODUCER";
    private static final String PRODUCER_EXTERNAL_ID_FIELD = "producer_external_id";

    private final ProducerRepository producerRepository;

    @Override
    public String validationTypeName() {
        return VALIDATION_TYPE_NAME;
    }

    @Override
    public ValidationExecutionResult executeValidation(ValidationTaskContext context) {
        List<ValidationError> errors = new ArrayList<>();

        for (RawImportRecordEntity raw : context.rawRecords()) {
            JsonNode payload = raw.getRawPayload();

            if (payload == null || !payload.isObject()) {
                continue;
            }

            JsonNode producerExternalIdNode = payload.get(PRODUCER_EXTERNAL_ID_FIELD);
            String producerExternalId = producerExternalIdNode == null || producerExternalIdNode.isNull()
                    ? null
                    : producerExternalIdNode.asText(null);

            if (producerExternalId == null || producerExternalId.isBlank()) {
                errors.add(ValidationError.row(
                        raw,
                        PRODUCER_EXTERNAL_ID_FIELD,
                        producerExternalId,
                        "El identificador externo del producer no fue informado."
                ));
                continue;
            }

            if (!producerRepository.existsByExternalProducerId(producerExternalId.trim())) {
                errors.add(ValidationError.row(
                        raw,
                        PRODUCER_EXTERNAL_ID_FIELD,
                        producerExternalId,
                        "El producer informado no existe."
                ));
            }
        }

        return errors.isEmpty()
                ? ValidationExecutionResult.success()
                : ValidationExecutionResult.failed(errors);
    }
}