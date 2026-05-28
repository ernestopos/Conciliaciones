package com.conciliaciones.msmanagementtask.infrastructure.scheduler.tasks;

import com.conciliaciones.domain.entity.RawImportRecordEntity;
import com.conciliaciones.domain.entity.SourceFileSheetEntity;
import com.conciliaciones.msmanagementtask.application.port.out.storage.ObjectStoragePort;
import com.conciliaciones.msmanagementtask.domain.model.storage.StoredObjectContent;
import com.conciliaciones.persistence.jpa.entity.ScheduledTaskEntity;
import com.conciliaciones.persistence.jpa.entity.SourceFileEntity;
import com.conciliaciones.persistence.repository.ParameterRepository;
import com.conciliaciones.persistence.repository.RawImportRecordRepository;
import com.conciliaciones.persistence.repository.SourceFileRepository;
import com.conciliaciones.persistence.repository.SourceFileSheetRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("startUploadDataTask")
@RequiredArgsConstructor
@Transactional
public class StartUploadDataTask extends AbstractManagementTask {

    private static final String CSV_DELIMITER = "|";
    private static final String CSV_TECHNICAL_SHEET_NAME_FALLBACK = "CSV_DATA";
    private static final String PARSE_STATUS_GROUP = "PARSE_STATUS";
    private static final String PARSE_STATUS_PENDING = "PENDING";
    private static final String SYSTEM_USER = "startUploadDataTask";

    private final SourceFileRepository sourceFileRepository;
    private final SourceFileSheetRepository sourceFileSheetRepository;
    private final RawImportRecordRepository rawImportRecordRepository;
    private final ParameterRepository parameterRepository;
    private final ObjectStoragePort objectStoragePort;
    private final ObjectMapper objectMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    protected void doExecute(ScheduledTaskEntity task) {
        Long sourceFileId = task.getExecutionPlanTask().getSourceFile().getId();
        SourceFileEntity sourceFile = sourceFileRepository.findById(sourceFileId)
                .orElseThrow(() -> new IllegalStateException("No existe SourceFile con id " + sourceFileId));

        log.info("LOG INICIO X = startUploadDataTask.setData - sourceFileId={}, bucket={}, key={}, originalFileName={}",
                sourceFile.getId(), sourceFile.getS3Bucket(), sourceFile.getS3Key(), sourceFile.getOriginalFileName());

        validateS3Reference(sourceFile);

        StoredObjectContent storedObjectContent = objectStoragePort.readObject(sourceFile.getS3Bucket(), sourceFile.getS3Key());
        String fileContent = storedObjectContent.asString();

        CsvContent csvContent = parseCsvContent(fileContent);
        Long parsePendingStatusId = getParseStatusPendingId();

        cleanPreviousRawData(sourceFile.getId());

        SourceFileSheetEntity sourceFileSheet = createTechnicalCsvSheet(sourceFile, csvContent.dataRows().size());
        List<RawImportRecordEntity> rawImportRecords = buildRawImportRecords(
                sourceFile,
                sourceFileSheet,
                csvContent,
                parsePendingStatusId
        );

        rawImportRecordRepository.saveAll(rawImportRecords);

        sourceFile.setTotalRows(rawImportRecords.size());
        sourceFile.setProcessedRows(0);
        sourceFile.setFailedRows(0);
        sourceFile.setErrorMessage(null);
        sourceFileRepository.save(sourceFile);

        log.info("LOG FIN X = startUploadDataTask.setData - sourceFileId={}, sheetId={}, rawRows={}",
                sourceFile.getId(), sourceFileSheet.getId(), rawImportRecords.size());
    }

    private void validateS3Reference(SourceFileEntity sourceFile) {
        if (sourceFile.getS3Bucket() == null || sourceFile.getS3Bucket().isBlank()) {
            throw new IllegalStateException("El archivo no tiene bucket S3 asociado. sourceFileId=" + sourceFile.getId());
        }
        if (sourceFile.getS3Key() == null || sourceFile.getS3Key().isBlank()) {
            throw new IllegalStateException("El archivo no tiene key S3 asociado. sourceFileId=" + sourceFile.getId());
        }
        if (!objectStoragePort.bucketExists(sourceFile.getS3Bucket())) {
            throw new IllegalStateException("El bucket S3 no existe. sourceFileId=" + sourceFile.getId()
                    + ", bucket=" + sourceFile.getS3Bucket());
        }
        if (!objectStoragePort.objectExists(sourceFile.getS3Bucket(), sourceFile.getS3Key())) {
            throw new IllegalStateException("El archivo no existe en S3. sourceFileId=" + sourceFile.getId()
                    + ", bucket=" + sourceFile.getS3Bucket()
                    + ", key=" + sourceFile.getS3Key());
        }
    }

    private CsvContent parseCsvContent(String fileContent) {
        List<String> lines = fileContent.lines()
                .map(line -> line.replace("\uFEFF", ""))
                .filter(line -> line != null && !line.isBlank())
                .toList();

        if (lines.isEmpty()) {
            throw new IllegalStateException("El archivo CSV no contiene información para cargar.");
        }

        List<String> headers = parseDelimitedLine(lines.getFirst(), CSV_DELIMITER.charAt(0));
        List<List<String>> dataRows = new ArrayList<>();

        for (int index = 1; index < lines.size(); index++) {
            dataRows.add(parseDelimitedLine(lines.get(index), CSV_DELIMITER.charAt(0)));
        }

        return new CsvContent(headers, dataRows);
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

    private Long getParseStatusPendingId() {
        return parameterRepository.findByParameterGroupAndNameAndActiveTrue(PARSE_STATUS_GROUP, PARSE_STATUS_PENDING)
                .orElseThrow(() -> new IllegalStateException("No existe parámetro activo PARSE_STATUS/PENDING"))
                .getId();
    }

    private void cleanPreviousRawData(Long sourceFileId) {
        log.info("Limpiando registros crudos previos para sourceFileId={}", sourceFileId);

        entityManager.createQuery("DELETE FROM RawImportRecordEntity raw WHERE raw.sourceFileId = :sourceFileId")
                .setParameter("sourceFileId", sourceFileId)
                .executeUpdate();

        entityManager.createQuery("DELETE FROM SourceFileSheetEntity sheet WHERE sheet.sourceFileId = :sourceFileId")
                .setParameter("sourceFileId", sourceFileId)
                .executeUpdate();

        entityManager.flush();
    }

    private SourceFileSheetEntity createTechnicalCsvSheet(SourceFileEntity sourceFile, Integer totalRows) {
        String sheetName = resolveTechnicalSheetName(sourceFile);

        SourceFileSheetEntity sourceFileSheet = SourceFileSheetEntity.builder()
                .sourceFileId(sourceFile.getId())
                .sheetName(sheetName)
                .sheetOrder(1)
                .totalRows(totalRows)
                .createdAt(LocalDateTime.now())
                .createdBy(SYSTEM_USER)
                .build();

        return sourceFileSheetRepository.save(sourceFileSheet);
    }

    private String resolveTechnicalSheetName(SourceFileEntity sourceFile) {
        if (sourceFile.getOriginalFileName() != null && !sourceFile.getOriginalFileName().isBlank()) {
            return sourceFile.getOriginalFileName();
        }
        return CSV_TECHNICAL_SHEET_NAME_FALLBACK;
    }

    private List<RawImportRecordEntity> buildRawImportRecords(
            SourceFileEntity sourceFile,
            SourceFileSheetEntity sourceFileSheet,
            CsvContent csvContent,
            Long parsePendingStatusId
    ) {
        List<RawImportRecordEntity> rawImportRecords = new ArrayList<>();

        for (int index = 0; index < csvContent.dataRows().size(); index++) {
            int rowNumber = index + 1;
            List<String> rowValues = csvContent.dataRows().get(index);
            Map<String, String> rawPayload = buildRawPayload(csvContent.headers(), rowValues);

            rawImportRecords.add(RawImportRecordEntity.builder()
                    .sourceFileId(sourceFile.getId())
                    .sourceFileSheetId(sourceFileSheet.getId())
                    .rowNumber(rowNumber)
                    .sourceRowKey(buildSourceRowKey(sourceFile, rowNumber))
                    .rawPayload(objectMapper.valueToTree(rawPayload))
                    .parseStatusId(parsePendingStatusId)
                    .parseError(null)
                    .createdAt(LocalDateTime.now())
                    .createdBy(SYSTEM_USER)
                    .build());
        }

        return rawImportRecords;
    }

    private Map<String, String> buildRawPayload(List<String> headers, List<String> rowValues) {
        Map<String, String> rawPayload = new LinkedHashMap<>();

        for (int index = 0; index < headers.size(); index++) {
            String header = headers.get(index);
            String value = index < rowValues.size() ? rowValues.get(index) : null;
            rawPayload.put(header, value);
        }

        return rawPayload;
    }

    private String buildSourceRowKey(SourceFileEntity sourceFile, Integer rowNumber) {
        return sourceFile.getId() + "-" + rowNumber;
    }

    private record CsvContent(List<String> headers, List<List<String>> dataRows) {
    }
}
