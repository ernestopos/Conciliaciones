package com.conciliaciones.msmanagementtask.application.usecase;

import com.conciliaciones.domain.entity.ParameterEntity;
import com.conciliaciones.msmanagementtask.application.constants.ManagementTaskParameterGroups;
import com.conciliaciones.msmanagementtask.application.constants.ManagementTaskStatus;
import com.conciliaciones.persistence.jpa.entity.ExecutionPlanTaskEntity;
import com.conciliaciones.persistence.jpa.entity.ScheduledTaskEntity;
import com.conciliaciones.persistence.repository.ExecutionPlanTaskRepository;
import com.conciliaciones.persistence.repository.ParameterRepository;
import com.conciliaciones.persistence.repository.ScheduledTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PipelineTaskStatusService {

    private final ScheduledTaskRepository scheduledTaskRepository;
    private final ExecutionPlanTaskRepository executionPlanTaskRepository;
    private final ParameterRepository parameterRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markTaskAsProcess(Long scheduledTaskId) {
        ScheduledTaskEntity task = getTask(scheduledTaskId);
        task.setStatus(getParameter(ManagementTaskParameterGroups.SCHEDULED_TASK_STATUS,ManagementTaskStatus.PROCESS));
        task.setActive(Boolean.TRUE);
        task.setUpdatedBy(getClass().getSimpleName());
        scheduledTaskRepository.saveAndFlush(task);
        ExecutionPlanTaskEntity plan = task.getExecutionPlanTask();
        plan.setStatus(getParameter(ManagementTaskParameterGroups.EXECUTION_PLAN_TASK_STATUS,ManagementTaskStatus.PLAN_EXECUTING));
        plan.setMessage("Ejecutando tarea: " + task.getTaskType().getName());
        executionPlanTaskRepository.saveAndFlush(plan);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markTaskAsExecuted(Long scheduledTaskId) {
        ScheduledTaskEntity task = getTask(scheduledTaskId);
        task.setStatus(getParameter(ManagementTaskParameterGroups.SCHEDULED_TASK_STATUS,ManagementTaskStatus.EXECUTED));
        task.setActive(Boolean.FALSE);
        task.setUpdatedBy(getClass().getSimpleName());
        scheduledTaskRepository.saveAndFlush(task);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markTaskAsFailed(Long scheduledTaskId) {
        ScheduledTaskEntity task = getTask(scheduledTaskId);
        task.setStatus(getParameter(ManagementTaskParameterGroups.SCHEDULED_TASK_STATUS,ManagementTaskStatus.FAILED));
        task.setActive(Boolean.FALSE);
        task.setUpdatedBy(getClass().getSimpleName());
        scheduledTaskRepository.saveAndFlush(task);
        ExecutionPlanTaskEntity plan = task.getExecutionPlanTask();
        plan.setStatus(getParameter(ManagementTaskParameterGroups.EXECUTION_PLAN_TASK_STATUS,ManagementTaskStatus.PLAN_FAILED));
        plan.setSuccessful(Boolean.FALSE);
        plan.setFinishedAt(LocalDateTime.now());
        plan.setMessage("Falló la tarea: " + task.getTaskType().getName());
        executionPlanTaskRepository.saveAndFlush(plan);
    }

    private ScheduledTaskEntity getTask(Long scheduledTaskId) {
        return scheduledTaskRepository.findById(scheduledTaskId).orElseThrow(() -> new IllegalStateException("No existe ScheduledTask con id " + scheduledTaskId));
    }

    private ParameterEntity getParameter(String group, String name) {
        return parameterRepository.findByParameterGroupAndNameAndActiveTrue(group, name).orElseThrow(() -> new IllegalStateException("No existe parámetro activo. group=" + group + ", name=" + name));
    }
}