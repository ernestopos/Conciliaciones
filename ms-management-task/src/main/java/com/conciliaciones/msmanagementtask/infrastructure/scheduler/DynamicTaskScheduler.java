package com.conciliaciones.msmanagementtask.infrastructure.scheduler;

import com.conciliaciones.domain.task.ScheduledTaskStatus;
import com.conciliaciones.persistence.jpa.entity.ScheduledTaskEntity;
import com.conciliaciones.persistence.repository.ScheduledTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicTaskScheduler {

    private final ThreadPoolTaskScheduler taskScheduler;
    private final ScheduledTaskRepository scheduledTaskRepository;
    private final ApplicationContext applicationContext;
    private final Map<Long, ScheduledFuture<?>> scheduledFutures = new ConcurrentHashMap<>();

    @Scheduled(fixedDelayString = "${management-task.scheduler.refresh-fixed-delay-ms:15000}")
    @Transactional
    public void refreshScheduledTasks() {
        List<ScheduledTaskEntity> activeTasks = scheduledTaskRepository.findByActiveTrueAndStatusIn(
                List.of(ScheduledTaskStatus.PENDING, ScheduledTaskStatus.SCHEDULED)
        );

        for (ScheduledTaskEntity task : activeTasks) {
            if (!scheduledFutures.containsKey(task.getId())) {
                schedule(task);
            }
        }

        scheduledFutures.keySet().removeIf(taskId -> {
            boolean stillActive = activeTasks.stream().anyMatch(task -> task.getId().equals(taskId));
            if (!stillActive) {
                ScheduledFuture<?> future = scheduledFutures.get(taskId);
                if (future != null) future.cancel(false);
                return true;
            }
            return false;
        });
    }

    private void schedule(ScheduledTaskEntity task) {
        log.info("Programando tarea. id={}, bean={}, cron={}", task.getId(), task.getTaskBeanName(), task.getCronExpression());

        ScheduledTaskRunnable runnable = applicationContext.getBean(task.getTaskBeanName(), ScheduledTaskRunnable.class);

        ScheduledFuture<?> future = taskScheduler.schedule(
                () -> runnable.execute(task),
                new CronTrigger(task.getCronExpression())
        );

        scheduledFutures.put(task.getId(), future);
        task.setStatus(ScheduledTaskStatus.SCHEDULED);
        task.setUpdatedBy("SCHEDULER");
        scheduledTaskRepository.save(task);
    }
}
