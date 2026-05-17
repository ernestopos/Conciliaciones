package com.conciliaciones.msmanagementtask.infrastructure.scheduler.tasks;

import com.conciliaciones.domain.entity.ParameterEntity;
import com.conciliaciones.msmanagementtask.application.constants.ManagementTaskParameterGroups;
import com.conciliaciones.msmanagementtask.application.constants.ManagementTaskStatus;
import com.conciliaciones.persistence.jpa.entity.ExecutionPlanTaskEntity;
import com.conciliaciones.persistence.jpa.entity.ScheduledTaskEntity;
import com.conciliaciones.persistence.repository.ExecutionPlanTaskRepository;
import com.conciliaciones.persistence.repository.ParameterRepository;
import com.conciliaciones.persistence.repository.ScheduledTaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("startPolicyRulesTask")
public class StartPolicyRulesTask extends AbstractManagementTask {

    private ScheduledTaskRepository scheduledTaskRepository;
    private ExecutionPlanTaskRepository executionPlanTaskRepository;
    private ParameterRepository parameterRepository;

    public StartPolicyRulesTask(ScheduledTaskRepository scheduledTaskRepository,ExecutionPlanTaskRepository executionPlanTaskRepository,ParameterRepository parameterRepository){
        this.scheduledTaskRepository=scheduledTaskRepository;
        this.executionPlanTaskRepository=executionPlanTaskRepository;
        this.parameterRepository=parameterRepository;
    }

    @Override
    protected void doExecute(ScheduledTaskEntity task) {
        markTaskAsProcess(task);
        log.info("Ejecutando reglas de póliza/comisión. executionPlanTaskId={}, taskId={}", task.getExecutionPlanTask().getId(), task.getId());
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

    private ParameterEntity getParameter(String group, String name) {
        return parameterRepository.findByParameterGroupAndNameAndActiveTrue(group, name)
                .orElseThrow(() -> new IllegalStateException("No existe parámetro activo. group=" + group + ", name=" + name));
    }
}
