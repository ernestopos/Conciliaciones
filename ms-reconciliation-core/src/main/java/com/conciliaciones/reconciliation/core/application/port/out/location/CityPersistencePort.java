package com.conciliaciones.reconciliation.core.application.port.out.location;

import com.conciliaciones.domain.entity.CityEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CityPersistencePort {
    Optional<CityEntity> findById(Long id);
    Page<CityEntity> searchByState(Long stateId, String name, Pageable pageable);
}