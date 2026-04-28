package com.conciliaciones.persistence.repository;

import com.conciliaciones.persistence.jpa.entity.TaskExecutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskExecutionRepository extends JpaRepository<TaskExecutionEntity, Long> {
}
