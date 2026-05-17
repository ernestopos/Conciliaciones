package com.conciliaciones.msmanagementtask.application.usecase;

import com.conciliaciones.domain.entity.ParameterEntity;
import com.conciliaciones.msmanagementtask.application.constants.ManagementTaskParameterGroups;
import com.conciliaciones.msmanagementtask.application.constants.ManagementTaskStatus;
import com.conciliaciones.msmanagementtask.application.constants.TaskDefinition;
import com.conciliaciones.msmanagementtask.application.event.ExecutionPlanStatusEvent;
import com.conciliaciones.msmanagementtask.application.event.TaskExecutionRequestedEvent;
import com.conciliaciones.msmanagementtask.application.port.out.ExecutionPlanEventPublisher;
import com.conciliaciones.msmanagementtask.application.port.out.TaskExecutionEventPublisher;
import com.conciliaciones.msmanagementtask.infrastructure.scheduler.ScheduledTaskRunnable;
import com.conciliaciones.persistence.jpa.entity.ExecutionPlanTaskEntity;
import com.conciliaciones.persistence.jpa.entity.ScheduledTaskEntity;
import com.conciliaciones.persistence.repository.ExecutionPlanTaskRepository;
import com.conciliaciones.persistence.repository.ParameterRepository;
import com.conciliaciones.persistence.repository.ScheduledTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineTaskExecutorService {

    private final ScheduledTaskRepository scheduledTaskRepository;
    private final ExecutionPlanTaskRepository executionPlanTaskRepository;
    private final ParameterRepository parameterRepository;
    private final ExecutionPlanEventPublisher executionPlanEventPublisher;
    private final TaskExecutionEventPublisher taskExecutionEventPublisher;
    private final ApplicationContext applicationContext;

    @Transactional
    public void execute(Long scheduledTaskId) {
        ScheduledTaskEntity task = scheduledTaskRepository.findById(scheduledTaskId)
                .orElseThrow(() -> new IllegalStateException("No existe ScheduledTask con id " + scheduledTaskId));

        Long executionPlanTaskId = task.getExecutionPlanTask().getId();
        String taskTypeName = task.getTaskType().getName();

        log.info("LOG INICIO X = executePipelineTask - taskId={}, executionPlanTaskId={}, taskType={}",
                task.getId(), executionPlanTaskId, taskTypeName);

        if (!canStart(task)) {
            log.info("Tarea ignorada porque no está lista para iniciar. taskId={}, executionPlanTaskId={}, taskType={}, status={}, active={}",
                    task.getId(), executionPlanTaskId, taskTypeName, task.getStatus().getName(), task.getActive());
            return;
        }

        try {
            validateSequentialExecution(task);
            publishPlanStatus(task.getExecutionPlanTask(), "Tarea en proceso: " + taskTypeName);
            markTaskAsProcess(task);
            ScheduledTaskRunnable runnable = applicationContext.getBean(task.getTaskBeanName(), ScheduledTaskRunnable.class);
            runnable.execute(task);
            sleepDemoExecution();
            markTaskAsExecuted(task);
            activateNextTaskOrClosePlan(task);
            log.info("LOG FIN X = executePipelineTask - taskId={}, executionPlanTaskId={}, taskType={}",
                    task.getId(), executionPlanTaskId, taskTypeName);
        } catch (Exception ex) {
            log.error("Error ejecutando tarea. taskId={}, executionPlanTaskId={}, taskType={}",
                    task.getId(), executionPlanTaskId, taskTypeName, ex);
            markTaskAsFailed(task, ex.getMessage());
            markPlanAsFailed(task.getExecutionPlanTask(), ex.getMessage());
            publishPlanStatus(task.getExecutionPlanTask(), ex.getMessage());
        }
    }

    private void sleepDemoExecution() {
        try {
            Thread.sleep(20_000); // 2 minutos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("La tarea fue interrumpida durante la pausa de demo", e);
        }
    }

    private boolean canStart(ScheduledTaskEntity task) {
        return Boolean.TRUE.equals(task.getActive())
                && List.of(ManagementTaskStatus.PENDING, ManagementTaskStatus.SCHEDULED).contains(task.getStatus().getName());
    }

    private void validateSequentialExecution(ScheduledTaskEntity task) {
        Long planId = task.getExecutionPlanTask().getId();

        long processCount = scheduledTaskRepository.countByExecutionPlanTask_IdAndStatus_NameAndIdNot(
                planId,
                ManagementTaskStatus.PROCESS,
                task.getId()
        );

        if (processCount > 0) {
            throw new IllegalStateException("No se puede ejecutar la tarea porque ya existe otra tarea en PROCESS para executionPlanTaskId=" + planId);
        }

        TaskDefinition current = TaskDefinition.fromParameterName(task.getTaskType().getName());
        TaskDefinition previous = current.previous();

        if (previous == null) {
            return;
        }

        ScheduledTaskEntity previousTask = scheduledTaskRepository
                .findFirstByExecutionPlanTask_IdAndTaskType_NameOrderByIdAsc(planId, previous.getParameterName())
                .orElseThrow(() -> new IllegalStateException("No existe la tarea anterior " + previous.getParameterName()
                        + " para executionPlanTaskId=" + planId));

        if (!ManagementTaskStatus.EXECUTED.equals(previousTask.getStatus().getName())) {
            throw new IllegalStateException("No se puede ejecutar " + current.getParameterName()
                    + " porque la tarea anterior " + previous.getParameterName()
                    + " está en estado " + previousTask.getStatus().getName());
        }
    }

    private void markTaskAsProcess(ScheduledTaskEntity task) {
        task.setStatus(getParameter(ManagementTaskParameterGroups.SCHEDULED_TASK_STATUS, ManagementTaskStatus.PROCESS));
        task.setActive(Boolean.TRUE);
        task.setUpdatedBy(getClass().getSimpleName());
        scheduledTaskRepository.save(task);

        ExecutionPlanTaskEntity plan = task.getExecutionPlanTask();
        plan.setStatus(getParameter(ManagementTaskParameterGroups.EXECUTION_PLAN_TASK_STATUS, ManagementTaskStatus.PLAN_EXECUTING));
        plan.setMessage("Ejecutando tarea: " + task.getTaskType().getName());
        executionPlanTaskRepository.save(plan);
    }

    private void markTaskAsExecuted(ScheduledTaskEntity task) {
        task.setStatus(getParameter(ManagementTaskParameterGroups.SCHEDULED_TASK_STATUS, ManagementTaskStatus.EXECUTED));
        task.setActive(Boolean.FALSE);
        task.setUpdatedBy(getClass().getSimpleName());
        scheduledTaskRepository.save(task);
    }

    private void activateNextTaskOrClosePlan(ScheduledTaskEntity task) {
        TaskDefinition current = TaskDefinition.fromParameterName(task.getTaskType().getName());
        TaskDefinition next = current.next();

        if (next == null) {
            markPlanAsExecuted(task.getExecutionPlanTask());
            publishPlanStatus(task.getExecutionPlanTask(), "Plan de ejecución finalizado correctamente");
            return;
        }

        ScheduledTaskEntity nextTask = scheduledTaskRepository
                .findFirstByExecutionPlanTask_IdAndTaskType_NameOrderByIdAsc(
                        task.getExecutionPlanTask().getId(),
                        next.getParameterName()
                )
                .orElseThrow(() -> new IllegalStateException("No existe la siguiente tarea " + next.getParameterName()
                        + " para executionPlanTaskId=" + task.getExecutionPlanTask().getId()));

        nextTask.setStatus(getParameter(ManagementTaskParameterGroups.SCHEDULED_TASK_STATUS, ManagementTaskStatus.PENDING));
        nextTask.setActive(Boolean.TRUE);
        nextTask.setUpdatedBy(getClass().getSimpleName());
        scheduledTaskRepository.save(nextTask);

        log.info("Siguiente tarea activada. executionPlanTaskId={}, nextTaskId={}, nextTaskType={}",
                task.getExecutionPlanTask().getId(), nextTask.getId(), next.getParameterName());

        publishPlanStatus(task.getExecutionPlanTask(), "Siguiente tarea activada: " + next.getParameterName());
        publishTaskExecutionRequested(nextTask, "NEXT_TASK_REQUESTED");
    }

    private void markTaskAsFailed(ScheduledTaskEntity task, String message) {
        task.setStatus(getParameter(ManagementTaskParameterGroups.SCHEDULED_TASK_STATUS, ManagementTaskStatus.FAILED));
        task.setActive(Boolean.FALSE);
        task.setUpdatedBy(getClass().getSimpleName());
        scheduledTaskRepository.save(task);
    }

    private void markPlanAsExecuted(ExecutionPlanTaskEntity plan) {
        plan.setStatus(getParameter(ManagementTaskParameterGroups.EXECUTION_PLAN_TASK_STATUS, ManagementTaskStatus.PLAN_EXECUTED));
        plan.setSuccessful(Boolean.TRUE);
        plan.setFinishedAt(LocalDateTime.now());
        plan.setMessage("Plan de ejecución finalizado correctamente");
        executionPlanTaskRepository.save(plan);
    }

    private void markPlanAsFailed(ExecutionPlanTaskEntity plan, String message) {
        plan.setStatus(getParameter(ManagementTaskParameterGroups.EXECUTION_PLAN_TASK_STATUS, ManagementTaskStatus.PLAN_FAILED));
        plan.setSuccessful(Boolean.FALSE);
        plan.setFinishedAt(LocalDateTime.now());
        plan.setMessage(message);
        executionPlanTaskRepository.save(plan);
    }

    private void publishTaskExecutionRequested(ScheduledTaskEntity task, String eventType) {
        taskExecutionEventPublisher.publish(new TaskExecutionRequestedEvent(
                task.getExecutionPlanTask().getId(),
                task.getId(),
                task.getExecutionPlanTask().getSourceFile().getId(),
                task.getTaskType().getName(),
                eventType,
                LocalDateTime.now()
        ));
    }

    private void publishPlanStatus(ExecutionPlanTaskEntity plan, String message) {
        List<ScheduledTaskEntity> tasks = scheduledTaskRepository.findByExecutionPlanTask_IdOrderByIdAsc(plan.getId());

        List<ExecutionPlanStatusEvent.ScheduledTaskStatus> taskStatuses = tasks.stream()
                .sorted(Comparator.comparing(ScheduledTaskEntity::getTaskOrder, Comparator.nullsLast(Integer::compareTo)))
                .map(task -> new ExecutionPlanStatusEvent.ScheduledTaskStatus(
                        task.getId(),
                        task.getTaskType().getName(),
                        task.getStatus().getName(),
                        task.getActive(),
                        task.getTaskOrder() != null
                                ? task.getTaskOrder()
                                : TaskDefinition.fromParameterName(task.getTaskType().getName()).getOrder()
                ))
                .toList();

        executionPlanEventPublisher.publish(new ExecutionPlanStatusEvent(
                plan.getId(),
                plan.getSourceFile().getId(),
                plan.getPlanExecuteCode(),
                plan.getStatus().getName(),
                plan.getSuccessful(),
                message,
                LocalDateTime.now(),
                taskStatuses
        ));
    }

    private ParameterEntity getParameter(String group, String name) {
        return parameterRepository.findByParameterGroupAndNameAndActiveTrue(group, name)
                .orElseThrow(() -> new IllegalStateException("No existe parámetro activo. group=" + group + ", name=" + name));
    }
}
