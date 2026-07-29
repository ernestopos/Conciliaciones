package com.conciliaciones.reconciliation.core.application.port.out.carrierPortal;

import com.conciliaciones.domain.entity.CarrierPortalEntity;

import java.util.List;
import java.util.Optional;

public interface CarrierPortalPersistencePort {

    List<CarrierPortalEntity> findActive();

    Optional<CarrierPortalEntity> findById(Long id);
}