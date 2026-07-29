package com.conciliaciones.reconciliation.core.infrastructure.adapter.out.persistence.carrierPortal;

import com.conciliaciones.domain.entity.CarrierPortalEntity;
import com.conciliaciones.persistence.repository.CarrierPortalRepository;
import com.conciliaciones.reconciliation.core.application.port.out.carrierPortal.CarrierPortalPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class CarrierPortalPersistenceAdapter implements CarrierPortalPersistencePort {

    private final CarrierPortalRepository repository;

    @Override
    public List<CarrierPortalEntity> findActive() {
        log.info("LOG INICIO X = findActiveCarrierPortalsPersistence");
        List<CarrierPortalEntity> result = repository.findByActiveTrueOrderBySortOrderAscDisplayNameAsc();
        log.info("LOG FIN X = findActiveCarrierPortalsPersistence total={}",result.size());
        return result;
    }

    @Override
    public Optional<CarrierPortalEntity> findById(Long id) {
        log.info("LOG INICIO X = findCarrierPortalByIdPersistence id={}",id);
        Optional<CarrierPortalEntity> result = repository.findById(id);
        log.info("LOG FIN X = findCarrierPortalByIdPersistence found={}",result.isPresent());
        return result;
    }
}