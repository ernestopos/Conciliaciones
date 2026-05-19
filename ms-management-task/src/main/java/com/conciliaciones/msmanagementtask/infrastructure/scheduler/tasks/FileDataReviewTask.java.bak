package com.conciliaciones.msmanagementtask.infrastructure.scheduler.tasks;

import com.conciliaciones.domain.task.ScheduledTaskStatus;
import com.conciliaciones.msmanagementtask.infrastructure.scheduler.ScheduledTaskRunnable;
import com.conciliaciones.persistence.jpa.entity.ScheduledTaskEntity;
import com.conciliaciones.persistence.jpa.entity.TaskExecutionEntity;
import com.conciliaciones.persistence.repository.ScheduledTaskRepository;
import com.conciliaciones.persistence.repository.SourceFileRepository;
import com.conciliaciones.persistence.repository.TaskExecutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component("fileDataReviewTask")
@RequiredArgsConstructor
public class FileDataReviewTask implements ScheduledTaskRunnable {

    private final ScheduledTaskRepository scheduledTaskRepository;
    private final TaskExecutionRepository taskExecutionRepository;
    private final SourceFileRepository sourceFileRepository;

    @Override
    @Transactional
    public void execute(ScheduledTaskEntity task) {
        log.info("LOG INICIO X = execute FileDataReviewTask - taskId={}, sourceFileId={}",
                task.getId(), task.getSourceFileId());

        TaskExecutionEntity execution = taskExecutionRepository.save(TaskExecutionEntity.builder()
                .scheduledTaskId(task.getId())
                .startedAt(LocalDateTime.now())
                .build());

        try {
            task.setStatus(ScheduledTaskStatus.RUNNING);
            scheduledTaskRepository.save(task);

            sourceFileRepository.findById(task.getSourceFileId())
                    .orElseThrow(() -> new IllegalStateException("No existe SourceFile con id " + task.getSourceFileId()));

            // TODO mani:
            // 1. Descargar/leer archivo desde S3.
            // 2. Validar estructura del archivo.
            // 3. Validar hojas/columnas obligatorias.
            // 4. Validar tipos de datos y registros vacíos.
            // 5. Guardar resultado en tablas de detalle cuando las definamos.

            task.setStatus(ScheduledTaskStatus.FINISHED);
            task.setActive(Boolean.FALSE);
            task.setUpdatedBy("FileDataReviewTask");
            scheduledTaskRepository.save(task);

            execution.setSuccessful(Boolean.TRUE);
            execution.setFinishedAt(LocalDateTime.now());
            execution.setMessage("Revisión inicial finalizada correctamente");
            taskExecutionRepository.save(execution);
        } catch (Exception ex) {
            log.error("Error ejecutando FileDataReviewTask. taskId={}", task.getId(), ex);

            task.setStatus(ScheduledTaskStatus.FAILED);
            task.setActive(Boolean.FALSE);
            task.setUpdatedBy("FileDataReviewTask");
            scheduledTaskRepository.save(task);

            execution.setSuccessful(Boolean.FALSE);
            execution.setFinishedAt(LocalDateTime.now());
            execution.setMessage(ex.getMessage());
            taskExecutionRepository.save(execution);
        }
    }
}
