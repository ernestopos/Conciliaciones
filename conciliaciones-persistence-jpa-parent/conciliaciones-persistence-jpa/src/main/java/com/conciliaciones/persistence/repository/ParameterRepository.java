package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.ParameterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParameterRepository extends JpaRepository<ParameterEntity, Long> {
    Optional<ParameterEntity> findByParameterGroupAndNameAndActiveTrue(String parameterGroup, String name);
}
