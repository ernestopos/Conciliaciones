package com.conciliaciones.msmanagementtask.infrastructure.scheduler.validation;

import com.conciliaciones.domain.entity.ParameterEntity;
import com.conciliaciones.msmanagementtask.application.port.out.storage.ObjectStoragePort;
import com.conciliaciones.msmanagementtask.domain.model.storage.StoredObjectContent;
import com.conciliaciones.persistence.repository.ParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HeaderStructureValidationTask implements IValidationFile {

    private static final String VALIDATION_TYPE_NAME = "HEADER_STRUCTURE";
    private static final String CSV_DELIMITER = "|";

    private final ParameterRepository parameterRepository;
    private final ObjectStoragePort objectStoragePort;

    @Override
    public String validationTypeName() {
        return VALIDATION_TYPE_NAME;
    }

    @Override
    public ValidationExecutionResult executeValidation(ValidationTaskContext context) {
        List<ValidationError> errors = new ArrayList<>();

        List<ParameterEntity> expectedHeaders = parameterRepository
                .findByParameterGroupAndActiveTrueOrderBySortOrderAsc(ValidationConstants.VALIDATION_HEADER_STRUCTURE);

        if (expectedHeaders.isEmpty()) {
            return ValidationExecutionResult.failed(List.of(ValidationError.general(
                    "No existen parámetros activos para validar la estructura del encabezado.",
                    "parameterGroup=" + ValidationConstants.VALIDATION_HEADER_STRUCTURE
            )));
        }

        List<String> actualHeaders = extractHeadersFromS3File(context);
        validateExactHeaderStructure(expectedHeaders, actualHeaders, errors);

        return errors.isEmpty()
                ? ValidationExecutionResult.success()
                : ValidationExecutionResult.failed(errors);
    }

    private List<String> extractHeadersFromS3File(ValidationTaskContext context) {
        String bucket = context.sourceFile().getS3Bucket();
        String key = context.sourceFile().getS3Key();

        if (bucket == null || bucket.isBlank() || key == null || key.isBlank()) {
            throw new IllegalStateException("El archivo no tiene referencia S3 válida para validar encabezado. sourceFileId="
                    + context.sourceFile().getId());
        }

        StoredObjectContent storedObjectContent = objectStoragePort.readObject(bucket, key);
        String fileContent = storedObjectContent.asString();

        String headerLine = fileContent.lines()
                .map(line -> line.replace("\uFEFF", ""))
                .filter(line -> line != null && !line.isBlank())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("El archivo CSV no contiene encabezado. sourceFileId="
                        + context.sourceFile().getId()));

        return parseDelimitedLine(headerLine, CSV_DELIMITER.charAt(0)).stream()
                .map(this::normalizeHeader)
                .toList();
    }

    private List<String> parseDelimitedLine(String line, char delimiter) {
        List<String> values = new ArrayList<>();
        StringBuilder currentValue = new StringBuilder();
        boolean insideQuotes = false;

        for (int index = 0; index < line.length(); index++) {
            char currentChar = line.charAt(index);

            if (currentChar == '"') {
                if (insideQuotes && index + 1 < line.length() && line.charAt(index + 1) == '"') {
                    currentValue.append('"');
                    index++;
                } else {
                    insideQuotes = !insideQuotes;
                }
            } else if (currentChar == delimiter && !insideQuotes) {
                values.add(currentValue.toString().trim());
                currentValue.setLength(0);
            } else {
                currentValue.append(currentChar);
            }
        }

        values.add(currentValue.toString().trim());
        return values;
    }

    private void validateExactHeaderStructure(
            List<ParameterEntity> expectedHeaders,
            List<String> actualHeaders,
            List<ValidationError> errors
    ) {
        if (actualHeaders.size() != expectedHeaders.size()) {
            errors.add(ValidationError.general(
                    "La cantidad de columnas del encabezado no coincide con la estructura esperada.",
                    "expectedColumns=" + expectedHeaders.size()
                            + ", actualColumns=" + actualHeaders.size()
                            + ", expectedHeaders=" + expectedHeaderNames(expectedHeaders)
                            + ", actualHeaders=" + actualHeaders
            ));
        }

        int max = Math.max(expectedHeaders.size(), actualHeaders.size());
        for (int index = 0; index < max; index++) {
            String expectedHeader = index < expectedHeaders.size()
                    ? normalizeHeader(expectedHeaders.get(index).getName())
                    : null;
            String actualHeader = index < actualHeaders.size()
                    ? actualHeaders.get(index)
                    : null;

            if (expectedHeader == null) {
                errors.add(ValidationError.general(
                        "El archivo contiene una columna adicional no esperada en la posición " + (index + 1) + ": " + actualHeader,
                        "position=" + (index + 1) + ", actualHeader=" + actualHeader
                ));
                continue;
            }

            if (actualHeader == null) {
                errors.add(ValidationError.general(
                        "Falta la columna esperada en la posición " + (index + 1) + ": " + expectedHeader,
                        "position=" + (index + 1)
                                + ", expectedHeader=" + expectedHeader
                                + ", sortOrder=" + expectedHeaders.get(index).getSortOrder()
                ));
                continue;
            }

            if (!expectedHeader.equals(actualHeader)) {
                errors.add(ValidationError.general(
                        "La estructura del encabezado no es válida. Se esperaba " + expectedHeader
                                + " en la posición " + (index + 1)
                                + ", pero se recibió " + actualHeader + ".",
                        "position=" + (index + 1)
                                + ", expectedHeader=" + expectedHeader
                                + ", actualHeader=" + actualHeader
                                + ", sortOrder=" + expectedHeaders.get(index).getSortOrder()
                ));
            }
        }
    }

    private List<String> expectedHeaderNames(List<ParameterEntity> expectedHeaders) {
        return expectedHeaders.stream()
                .map(ParameterEntity::getName)
                .map(this::normalizeHeader)
                .toList();
    }

    private String normalizeHeader(String header) {
        if (header == null) {
            return null;
        }
        return header.replace("\uFEFF", "").trim();
    }
}
