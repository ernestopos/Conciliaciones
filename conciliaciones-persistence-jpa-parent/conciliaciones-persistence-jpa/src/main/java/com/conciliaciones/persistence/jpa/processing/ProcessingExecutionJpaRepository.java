package com.conciliaciones.persistence.jpa.processing;

import com.conciliaciones.persistence.jpa.entity.ProcessingExecutionEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessingExecutionJpaRepository extends JpaRepository<ProcessingExecutionEntity, Long> {

    Optional<ProcessingExecutionEntity> findByExecutionId(String executionId);

    List<ProcessingExecutionEntity> findBySourceFile_Id(Long sourceFileId);
}
