package com.conciliaciones.persistence.repository;

import com.conciliaciones.persistence.jpa.entity.ValidationSourcePlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ValidationSourcePlanRepository extends JpaRepository<ValidationSourcePlanEntity, Long> {

    Optional<ValidationSourcePlanEntity> findFirstBySourceFile_IdOrderByIdDesc(Long sourceFileId);
}
