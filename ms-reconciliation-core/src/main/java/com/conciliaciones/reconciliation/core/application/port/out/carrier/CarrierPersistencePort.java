package com.conciliaciones.reconciliation.core.application.port.out.carrier;

import com.conciliaciones.domain.entity.CarrierEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarrierPersistencePort {
    CarrierEntity save(CarrierEntity entity);
    Optional<CarrierEntity> findById(Long id);
    Page<CarrierEntity> findAll(Pageable pageable);
    void deleteById(Long id);
}
