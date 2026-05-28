package com.conciliaciones.persistence.repository;

import com.conciliaciones.persistence.jpa.entity.SourceFileTraceabilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SourceFileTraceabilityRepository extends JpaRepository<SourceFileTraceabilityEntity, Long> {

    List<SourceFileTraceabilityEntity> findBySourceFileIdOrderByCreatedAtAsc(Long sourceFileId);
}
