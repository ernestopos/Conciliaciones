package com.conciliaciones.msmanagementtask.infrastructure.scheduler.tasks;

import com.conciliaciones.domain.entity.ParameterEntity;
import com.conciliaciones.domain.entity.RawImportRecordEntity;
import com.conciliaciones.msmanagementtask.infrastructure.scheduler.validation.IValidationFile;
import com.conciliaciones.msmanagementtask.infrastructure.scheduler.validation.ValidationConstants;
import com.conciliaciones.msmanagementtask.infrastructure.scheduler.validation.ValidationError;
import com.conciliaciones.msmanagementtask.infrastructure.scheduler.validation.ValidationExecutionResult;
import com.conciliaciones.msmanagementtask.infrastructure.scheduler.validation.ValidationTaskContext;
import com.conciliaciones.persistence.jpa.entity.ScheduledTaskEntity;
import com.conciliaciones.persistence.jpa.entity.SourceFileEntity;
import com.conciliaciones.persistence.jpa.entity.SourceFileValidationEntity;
import com.conciliaciones.persistence.jpa.entity.ValidationSourcePlanEntity;
import com.conciliaciones.persistence.repository.ParameterRepository;
import com.conciliaciones.persistence.repository.RawImportRecordRepository;
import com.conciliaciones.persistence.repository.SourceFileRepository;
import com.conciliaciones.persistence.repository.SourceFileValidationRepository;
import com.conciliaciones.persistence.repository.ValidationSourcePlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component("startValidateDataTask")
@RequiredArgsConstructor
@Transactional
public class StartValidateDataTask extends AbstractManagementTask {

    private static final String SYSTEM_USER = "startValidateDataTask";

    private final SourceFileRepository sourceFileRepository;
    private final RawImportRecordRepository rawImportRecordRepository;
    private final ParameterRepository parameterRepository;
    private final ValidationSourcePlanRepository validationSourcePlanRepository;
    private final SourceFileValidationRepository sourceFileValidationRepository;
    private final List<IValidationFile> validationFiles;

    @Override
    protected void doExecute(ScheduledTaskEntity task) {
        Long executionPlanTaskId = task.getExecutionPlanTask().getId();
        Long sourceFileId = task.getExecutionPlanTask().getSourceFile().getId();

        log.info("LOG INICIO X = startValidateDataTask.validateData - executionPlanTaskId={}, taskId={}, sourceFileId={}",
                executionPlanTaskId, task.getId(), sourceFileId);

        SourceFileEntity sourceFile = sourceFileRepository.findById(sourceFileId)
                .orElseThrow(() -> new IllegalStateException("No existe SourceFile con id " + sourceFileId));

        ValidationSourcePlanEntity validationPlan = createValidationPlan(sourceFile);
        List<RawImportRecordEntity> rawRecords = rawImportRecordRepository.findBySourceFileIdOrderByRowNumberAsc(sourceFileId);

        Map<String, IValidationFile> validatorsByType = validationFiles.stream()
                .collect(Collectors.toMap(IValidationFile::validationTypeName, Function.identity(), (left, right) -> left));

        List<ParameterEntity> activeValidationTypes = parameterRepository
                .findByParameterGroupAndActiveTrueOrderBySortOrderAsc(ValidationConstants.SOURCE_FILE_VALIDATION_TYPE);

        ValidationTaskContext context = new ValidationTaskContext(sourceFile, validationPlan, rawRecords);

        for (ParameterEntity validationType : activeValidationTypes) {
            IValidationFile validator = validatorsByType.get(validationType.getName());
            if (validator == null) {
                log.warn("No existe implementación IValidationFile para validationType={}. Se omite temporalmente.", validationType.getName());
                continue;
            }

            log.info("Ejecutando validación. sourceFileId={}, validationType={}", sourceFileId, validationType.getName());
            ValidationExecutionResult result = validator.executeValidation(context);
            persistValidationResult(validationPlan, validationType, result);

            if (!result.successful()) {
                markPlanAsFailed(validationPlan, "Falló validación: " + validationType.getName());
                throw new IllegalStateException("Falló validación " + validationType.getName() + " para sourceFileId=" + sourceFileId);
            }
        }

        markPlanAsExecuted(validationPlan);
        log.info("LOG FIN X = startValidateDataTask.validateData - validationPlanId={}, sourceFileId={}, rawRows={}",
                validationPlan.getId(), sourceFileId, rawRecords.size());
    }

    private ValidationSourcePlanEntity createValidationPlan(SourceFileEntity sourceFile) {
        ParameterEntity processStatus = getParameter(ValidationConstants.VALIDATION_SOURCE_PLAN_STATUS, ValidationConstants.PROCESS);
        ValidationSourcePlanEntity validationPlan = ValidationSourcePlanEntity.builder()
                .sourceFile(sourceFile)
                .status(processStatus)
                .startedAt(LocalDateTime.now())
                .successful(null)
                .message("Validación de archivo en proceso")
                .createdAt(LocalDateTime.now())
                .createdBy(SYSTEM_USER)
                .build();
        return validationSourcePlanRepository.saveAndFlush(validationPlan);
    }

    private void persistValidationResult(ValidationSourcePlanEntity validationPlan, ParameterEntity validationType, ValidationExecutionResult result) {
        ParameterEntity validationStatus = getParameter(
                ValidationConstants.SOURCE_FILE_VALIDATION_STATUS,
                result.successful() ? ValidationConstants.SUCCESS : ValidationConstants.ERROR
        );

        if (result.successful()) {
            sourceFileValidationRepository.save(SourceFileValidationEntity.builder()
                    .validationSourcePlan(validationPlan)
                    .rawImportRecordEntity(null)
                    .validationTypeId(validationType)
                    .validationStatusId(validationStatus.getId())
                    .message("Validación ejecutada correctamente")
                    .createdAt(LocalDateTime.now())
                    .createdBy(SYSTEM_USER)
                    .build());
            return;
        }

        for (ValidationError error : result.errors()) {
            SourceFileValidationEntity entity = new SourceFileValidationEntity();
            entity.setValidationSourcePlan(validationPlan);
            entity.setRawImportRecordEntity(error.rawImportRecord());
            entity.setValidationTypeId(validationType);
            entity.setValidationStatusId(validationStatus.getId());
            entity.setRowNumber(error.rowNumber());
            entity.setColumnName(error.columnName());
            entity.setFieldValue(error.fieldValue());
            entity.setMessage(error.message());
            entity.setTechnicalDetail(error.technicalDetail());
            entity.setCreatedAt(LocalDateTime.now());
            entity.setCreatedBy(SYSTEM_USER);
            sourceFileValidationRepository.save(entity);
        }
    }

    private void markPlanAsExecuted(ValidationSourcePlanEntity validationPlan) {
        validationPlan.setStatus(getParameter(ValidationConstants.VALIDATION_SOURCE_PLAN_STATUS, ValidationConstants.EXECUTED));
        validationPlan.setSuccessful(Boolean.TRUE);
        validationPlan.setFinishedAt(LocalDateTime.now());
        validationPlan.setMessage("Validaciones ejecutadas correctamente");
        validationPlan.setUpdatedAt(LocalDateTime.now());
        validationPlan.setUpdatedBy(SYSTEM_USER);
        validationSourcePlanRepository.saveAndFlush(validationPlan);
    }

    private void markPlanAsFailed(ValidationSourcePlanEntity validationPlan, String message) {
        validationPlan.setStatus(getParameter(ValidationConstants.VALIDATION_SOURCE_PLAN_STATUS, ValidationConstants.FAILED));
        validationPlan.setSuccessful(Boolean.FALSE);
        validationPlan.setFinishedAt(LocalDateTime.now());
        validationPlan.setMessage(message);
        validationPlan.setUpdatedAt(LocalDateTime.now());
        validationPlan.setUpdatedBy(SYSTEM_USER);
        validationSourcePlanRepository.saveAndFlush(validationPlan);
    }

    private ParameterEntity getParameter(String group, String name) {
        return parameterRepository.findByParameterGroupAndNameAndActiveTrue(group, name)
                .orElseThrow(() -> new IllegalStateException("No existe parámetro activo. group=" + group + ", name=" + name));
    }
}
