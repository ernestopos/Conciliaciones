package com.conciliaciones.persistence.repository;

import com.conciliaciones.persistence.jpa.entity.ExecutionPlanTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ExecutionPlanTaskRepository extends JpaRepository<ExecutionPlanTaskEntity, Long> {

    @Query("""
            SELECT ept
              FROM ExecutionPlanTaskEntity ept
              LEFT JOIN FETCH ept.scheduledTasks st
             WHERE ept.id = :id
            """)
    Optional<ExecutionPlanTaskEntity> findByIdWithScheduledTasks(@Param("id") Long id);

    Optional<ExecutionPlanTaskEntity> findFirstBySourceFile_IdOrderByIdDesc(Long sourceFileId);
}
