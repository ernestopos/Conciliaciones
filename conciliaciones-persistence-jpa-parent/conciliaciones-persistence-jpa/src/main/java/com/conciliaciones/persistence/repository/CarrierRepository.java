package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.CarrierEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarrierRepository extends JpaRepository<CarrierEntity, Long> {
}
