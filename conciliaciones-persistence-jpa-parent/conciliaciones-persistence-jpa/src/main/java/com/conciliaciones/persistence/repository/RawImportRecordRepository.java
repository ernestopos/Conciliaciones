package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.RawImportRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawImportRecordRepository extends JpaRepository<RawImportRecordEntity, Long> {
}
