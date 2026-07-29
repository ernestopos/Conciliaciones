package com.conciliaciones.mssecurity.infrastructure.adapter.out.persistence;

import com.conciliaciones.domain.entity.CarrierPortalEntity;
import com.conciliaciones.mssecurity.application.port.out.CarrierPortalQueryPort;
import com.conciliaciones.persistence.repository.CarrierPortalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CarrierPortalPersistenceAdapter implements CarrierPortalQueryPort {

    private final CarrierPortalRepository repository;

    @Override
    public Optional<CarrierPortalEntity> findActiveById(Long carrierPortalId) {
        return repository.findById(carrierPortalId)
                .filter(portal -> Boolean.TRUE.equals(portal.getActive()));
    }
}