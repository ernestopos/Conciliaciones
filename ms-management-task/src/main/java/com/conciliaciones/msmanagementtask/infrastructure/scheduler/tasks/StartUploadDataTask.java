package com.conciliaciones.msmanagementtask.infrastructure.scheduler.tasks;

import com.conciliaciones.persistence.jpa.entity.ScheduledTaskEntity;
import com.conciliaciones.persistence.jpa.entity.SourceFileEntity;
import com.conciliaciones.persistence.repository.SourceFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("startUploadDataTask")
@RequiredArgsConstructor
public class StartUploadDataTask extends AbstractManagementTask {

    private final SourceFileRepository sourceFileRepository;

    @Override
    protected void doExecute(ScheduledTaskEntity task) {
        Long sourceFileId = task.getExecutionPlanTask().getSourceFile().getId();
        SourceFileEntity sourceFile = sourceFileRepository.findById(sourceFileId)
                .orElseThrow(() -> new IllegalStateException("No existe SourceFile con id " + sourceFileId));

        log.info("Validando carga S3. sourceFileId={}, bucket={}, key={}, originalFileName={}",
                sourceFile.getId(), sourceFile.getS3Bucket(), sourceFile.getS3Key(), sourceFile.getOriginalFileName());

        if (sourceFile.getS3Bucket() == null || sourceFile.getS3Bucket().isBlank()) {
            throw new IllegalStateException("El archivo no tiene bucket S3 asociado. sourceFileId=" + sourceFileId);
        }
        if (sourceFile.getS3Key() == null || sourceFile.getS3Key().isBlank()) {
            throw new IllegalStateException("El archivo no tiene key S3 asociado. sourceFileId=" + sourceFileId);
        }
    }
}
