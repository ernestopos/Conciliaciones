package com.conciliaciones.msmanagementtask.application.usecase;

import com.conciliaciones.domain.entity.ParameterEntity;
import com.conciliaciones.msmanagementtask.application.constants.ManagementTaskParameterGroups;
import com.conciliaciones.msmanagementtask.application.constants.ManagementTaskStatus;
import com.conciliaciones.msmanagementtask.application.constants.TaskDefinition;
import com.conciliaciones.msmanagementtask.application.port.in.CreateScheduledTaskCommand;
import com.conciliaciones.msmanagementtask.application.event.TaskExecutionRequestedEvent;
import com.conciliaciones.msmanagementtask.application.port.in.CreateScheduledTaskUseCase;
import com.conciliaciones.msmanagementtask.application.port.out.TaskExecutionEventPublisher;
import com.conciliaciones.persistence.jpa.entity.ExecutionPlanTaskEntity;
import com.conciliaciones.persistence.jpa.entity.ScheduledTaskEntity;
import com.conciliaciones.persistence.jpa.entity.SourceFileEntity;
import com.conciliaciones.persistence.repository.ExecutionPlanTaskRepository;
import com.conciliaciones.persistence.repository.ParameterRepository;
import com.conciliaciones.persistence.repository.ScheduledTaskRepository;
import com.conciliaciones.persistence.repository.SourceFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateScheduledTaskService implements CreateScheduledTaskUseCase {

    private static final String DEFAULT_CRON = "*/10 * * * * *";

    private final SourceFileRepository sourceFileRepository;
    private final ParameterRepository parameterRepository;
    private final ExecutionPlanTaskRepository executionPlanTaskRepository;
    private final ScheduledTaskRepository scheduledTaskRepository;
    private final TaskExecutionEventPublisher taskExecutionEventPublisher;

    @Override
    @Transactional
    public Long create(CreateScheduledTaskCommand command) {
        log.info("LOG INICIO X = create - sourceFileId={}", command.sourceFileId());

        if (command.sourceFileId() == null) {
            throw new IllegalArgumentException("El sourceFileId es obligatorio para crear el plan de ejecución");
        }

        return executionPlanTaskRepository.findFirstBySourceFile_IdOrderByIdDesc(command.sourceFileId())
                .map(existingPlan -> {
                    log.info("Ya existe plan de ejecución para sourceFileId={}. executionPlanTaskId={}",
                            command.sourceFileId(), existingPlan.getId());
                    return existingPlan.getId();
                })
                .orElseGet(() -> createExecutionPlanWithTasks(command));
    }

    private Long createExecutionPlanWithTasks(CreateScheduledTaskCommand command) {
        SourceFileEntity sourceFile = sourceFileRepository.findById(command.sourceFileId())
                .orElseThrow(() -> new IllegalStateException("No existe SourceFile con id " + command.sourceFileId()));

        ParameterEntity planStartedStatus = getParameter(
                ManagementTaskParameterGroups.EXECUTION_PLAN_TASK_STATUS,
                ManagementTaskStatus.PLAN_STARTED
        );

        ExecutionPlanTaskEntity executionPlanTask = executionPlanTaskRepository.save(ExecutionPlanTaskEntity.builder()
                .sourceFile(sourceFile)
                .status(planStartedStatus)
                .planExecuteCode(buildPlanExecuteCode(sourceFile.getId()))
                .startedAt(LocalDateTime.now())
                .successful(null)
                .message("Plan de ejecución creado para archivo: " + sourceFile.getOriginalFileName())
                .build());

        ScheduledTaskEntity firstTask = null;
        for (TaskDefinition definition : TaskDefinition.ordered()) {
            ScheduledTaskEntity createdTask = createScheduledTask(executionPlanTask, definition, command.payload(), command.createdBy());
            if (TaskDefinition.START_UPLOAD_DATA.equals(definition)) {
                firstTask = createdTask;
            }
        }

        publishFirstTaskAfterCommit(firstTask);

        log.info("Plan de ejecución creado. executionPlanTaskId={}, sourceFileId={}, scheduledTasks={}",
                executionPlanTask.getId(), sourceFile.getId(), TaskDefinition.ordered().size());

        return executionPlanTask.getId();
    }

    private ScheduledTaskEntity createScheduledTask(ExecutionPlanTaskEntity executionPlanTask,
                                                    TaskDefinition definition,
                                                    String payload,
                                                    String createdBy) {
        ParameterEntity taskType = getParameter(
                ManagementTaskParameterGroups.SCHEDULED_TASK_TYPE,
                definition.getParameterName()
        );

        boolean isFirstTask = TaskDefinition.START_UPLOAD_DATA.equals(definition);
        ParameterEntity status = getParameter(
                ManagementTaskParameterGroups.SCHEDULED_TASK_STATUS,
                isFirstTask ? ManagementTaskStatus.PENDING : ManagementTaskStatus.WAITING
        );

        return scheduledTaskRepository.save(ScheduledTaskEntity.builder()
                .executionPlanTask(executionPlanTask)
                .taskType(taskType)
                .status(status)
                .taskOrder(definition.getOrder())
                .cronExpression(DEFAULT_CRON)
                .taskBeanName(definition.getBeanName())
                .methodName(definition.getMethodName())
                .payload(payload)
                .active(isFirstTask)
                .createdBy(createdBy == null ? "SYSTEM" : createdBy)
                .createdAt(LocalDateTime.now())
                .build());
    }

    private void publishFirstTaskAfterCommit(ScheduledTaskEntity firstTask) {
        if (firstTask == null) {
            return;
        }

        Long executionPlanTaskId = firstTask.getExecutionPlanTask().getId();
        Long scheduledTaskId = firstTask.getId();
        Long sourceFileId = firstTask.getExecutionPlanTask().getSourceFile().getId();
        String taskType = firstTask.getTaskType().getName();

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                taskExecutionEventPublisher.publish(new TaskExecutionRequestedEvent(
                        executionPlanTaskId,
                        scheduledTaskId,
                        sourceFileId,
                        taskType,
                        "FIRST_TASK_REQUESTED",
                        LocalDateTime.now()
                ));
            }
        });
    }

    private ParameterEntity getParameter(String group, String name) {
        return parameterRepository.findByParameterGroupAndNameAndActiveTrue(group, name)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe parámetro activo. group=" + group + ", name=" + name));
    }

    private String buildPlanExecuteCode(Long sourceFileId) {
        return "PLAN-SOURCE-" + sourceFileId + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}
