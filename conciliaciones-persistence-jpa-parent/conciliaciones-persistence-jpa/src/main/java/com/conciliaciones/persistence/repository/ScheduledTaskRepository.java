package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.task.ScheduledTaskStatus;
import com.conciliaciones.domain.task.TaskType;
import com.conciliaciones.persistence.jpa.entity.ScheduledTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduledTaskRepository extends JpaRepository<ScheduledTaskEntity, Long> {
    List<ScheduledTaskEntity> findByActiveTrueAndStatusIn(List<ScheduledTaskStatus> statuses);
    boolean existsBySourceFileIdAndTaskTypeAndStatusIn(Long sourceFileId, TaskType taskType, List<ScheduledTaskStatus> statuses);
    Optional<ScheduledTaskEntity> findFirstBySourceFileIdAndTaskTypeOrderByIdDesc(Long sourceFileId, TaskType taskType);
}
