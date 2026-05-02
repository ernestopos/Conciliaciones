package com.conciliaciones.persistence.repository;

import com.conciliaciones.persistence.jpa.entity.SourceFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SourceFileRepository extends JpaRepository<SourceFileEntity, Long> {
}
