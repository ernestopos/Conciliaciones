package com.conciliaciones.msmanagementtask.infrastructure.scheduler.validation;

import com.conciliaciones.domain.entity.RawImportRecordEntity;
import com.conciliaciones.persistence.repository.CarrierRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ValidateCarrierValidationTask implements IValidationFile {

    private static final String VALIDATION_TYPE_NAME = "VALIDATE_CARRIER";
    private static final String CARRIER_CODE_FIELD = "carrier_code";
    private final CarrierRepository carrierRepository;

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

            JsonNode carrierCodeNode = payload.get(CARRIER_CODE_FIELD);
            String carrierCode = carrierCodeNode == null || carrierCodeNode.isNull()
                    ? null
                    : carrierCodeNode.asText(null);

            if (carrierCode == null || carrierCode.isBlank()) {
                errors.add(ValidationError.row(
                        raw,
                        CARRIER_CODE_FIELD,
                        carrierCode,
                        "El código del carrier no fue informado."
                ));
                continue;
            }

            if (!carrierRepository.existsByCode(carrierCode.trim())) {
                errors.add(ValidationError.row(
                        raw,
                        CARRIER_CODE_FIELD,
                        carrierCode,
                        "El carrier informado no existe."
                ));
            }
        }

        return errors.isEmpty()
                ? ValidationExecutionResult.success()
                : ValidationExecutionResult.failed(errors);
    }
}