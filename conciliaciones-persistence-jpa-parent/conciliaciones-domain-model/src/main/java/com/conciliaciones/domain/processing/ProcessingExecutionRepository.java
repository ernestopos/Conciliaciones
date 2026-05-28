package com.conciliaciones.domain.processing;

import java.util.List;
import java.util.Optional;

public interface ProcessingExecutionRepository {

    ProcessingExecution save(ProcessingExecution execution);

    Optional<ProcessingExecution> findByExecutionId(String executionId);

    List<ProcessingExecution> findBySourceFileId(Long sourceFileId);
}
