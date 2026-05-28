package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.RawImportRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RawImportRecordRepository extends JpaRepository<RawImportRecordEntity, Long> {

    List<RawImportRecordEntity> findBySourceFileIdOrderByRowNumberAsc(Long sourceFileId);

    long countBySourceFileId(Long sourceFileId);
}
