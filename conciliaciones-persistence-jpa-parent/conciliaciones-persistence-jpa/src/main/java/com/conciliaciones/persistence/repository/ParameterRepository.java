package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.ParameterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParameterRepository extends JpaRepository<ParameterEntity, Long> {
}
