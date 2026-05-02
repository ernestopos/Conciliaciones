package com.conciliaciones.msmanagementtask.application.usecase;

import com.conciliaciones.domain.task.ScheduledTaskStatus;
import com.conciliaciones.domain.task.TaskType;
import com.conciliaciones.msmanagementtask.application.port.in.CreateScheduledTaskCommand;
import com.conciliaciones.msmanagementtask.application.port.in.CreateScheduledTaskUseCase;
import com.conciliaciones.persistence.jpa.entity.ScheduledTaskEntity;
import com.conciliaciones.persistence.repository.ScheduledTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateScheduledTaskService implements CreateScheduledTaskUseCase {

    private static final String DEFAULT_CRON = "*/10 * * * * *";
    private static final String FILE_DATA_REVIEW_BEAN = "fileDataReviewTask";

    private final ScheduledTaskRepository scheduledTaskRepository;

    @Override
    @Transactional
    public Long create(CreateScheduledTaskCommand command) {
        log.info("LOG INICIO X = create - sourceFileId={}, taskType={}", command.sourceFileId(), command.taskType());

        if (command.sourceFileId() == null) {
            throw new IllegalArgumentException("El sourceFileId es obligatorio para crear la tarea programada");
        }

        TaskType taskType = command.taskType() == null ? TaskType.FILE_DATA_REVIEW : command.taskType();

        boolean exists = scheduledTaskRepository.existsBySourceFileIdAndTaskTypeAndStatusIn(
                command.sourceFileId(),
                taskType,
                List.of(ScheduledTaskStatus.PENDING, ScheduledTaskStatus.SCHEDULED, ScheduledTaskStatus.RUNNING)
        );

        if (exists) {
            return scheduledTaskRepository
                    .findFirstBySourceFileIdAndTaskTypeOrderByIdDesc(command.sourceFileId(), taskType)
                    .map(ScheduledTaskEntity::getId)
                    .orElseThrow();
        }

        ScheduledTaskEntity entity = ScheduledTaskEntity.builder()
                .sourceFileId(command.sourceFileId())
                .taskType(taskType)
                .status(ScheduledTaskStatus.PENDING)
                .cronExpression(DEFAULT_CRON)
                .taskBeanName(FILE_DATA_REVIEW_BEAN)
                .payload(command.payload())
                .active(Boolean.TRUE)
                .createdBy(command.createdBy() == null ? "SYSTEM" : command.createdBy())
                .createdAt(LocalDateTime.now())
                .build();

        ScheduledTaskEntity saved = scheduledTaskRepository.save(entity);
        log.info("Tarea programada creada. id={}, sourceFileId={}", saved.getId(), saved.getSourceFileId());
        return saved.getId();
    }
}
