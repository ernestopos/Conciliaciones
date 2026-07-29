package com.conciliaciones.mssecurity.application.port.out;

import com.conciliaciones.domain.entity.CarrierPortalEntity;

import java.util.Optional;

public interface CarrierPortalQueryPort {

    Optional<CarrierPortalEntity> findActiveById(Long carrierPortalId);
}