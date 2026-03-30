package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.AgencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgencyRepository extends JpaRepository<AgencyEntity, Long> {
}
