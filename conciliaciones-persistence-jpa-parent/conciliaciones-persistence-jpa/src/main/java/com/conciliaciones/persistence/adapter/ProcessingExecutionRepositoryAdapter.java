package com.conciliaciones.persistence.adapter;

import com.conciliaciones.domain.processing.ProcessingExecution;
import com.conciliaciones.domain.processing.ProcessingExecutionRepository;
import com.conciliaciones.persistence.jpa.processing.ProcessingExecutionJpaRepository;
import com.conciliaciones.persistence.mapper.ProcessingExecutionPersistenceMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProcessingExecutionRepositoryAdapter implements ProcessingExecutionRepository {

    private final ProcessingExecutionJpaRepository processingExecutionJpaRepository;
    private final ProcessingExecutionPersistenceMapper mapper;

    @Override
    public ProcessingExecution save(ProcessingExecution execution) {
        return mapper.toDomain(processingExecutionJpaRepository.save(mapper.toEntity(execution)));
    }

    @Override
    public Optional<ProcessingExecution> findByExecutionId(String executionId) {
        return processingExecutionJpaRepository.findByExecutionId(executionId).map(mapper::toDomain);
    }

    @Override
    public List<ProcessingExecution> findBySourceFileId(Long sourceFileId) {
        return processingExecutionJpaRepository.findBySourceFile_Id(sourceFileId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
