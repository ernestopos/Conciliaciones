package com.conciliaciones.persistence.repository;

import com.conciliaciones.persistence.jpa.entity.ScheduledTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduledTaskRepository extends JpaRepository<ScheduledTaskEntity, Long> {
    List<ScheduledTaskEntity> findByActiveTrueAndStatus_NameIn(List<String> statusNames);
    List<ScheduledTaskEntity> findByExecutionPlanTask_IdOrderByIdAsc(Long executionPlanTaskId);
    Optional<ScheduledTaskEntity> findFirstByExecutionPlanTask_IdAndTaskType_NameOrderByIdAsc(Long executionPlanTaskId, String taskTypeName);
    long countByExecutionPlanTask_IdAndStatus_NameAndIdNot(Long executionPlanTaskId, String statusName, Long excludedTaskId);
}
