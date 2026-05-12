package com.conciliaciones.persistence.repository;

import com.conciliaciones.persistence.jpa.entity.ExecutionPlanTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskExecutionRepository extends JpaRepository<ExecutionPlanTaskEntity, Long> {

    List<ExecutionPlanTaskEntity> findBySourceFile_IdOrderByStartedAtDesc(Long sourceFileId);
    List<ExecutionPlanTaskEntity> findByStatus_NameOrderByStartedAtDesc(String statusName);
}