package com.conciliaciones.reconciliation.core.infrastructure.adapter.out.persistence.carrier;

import com.conciliaciones.domain.entity.CarrierEntity;
import com.conciliaciones.persistence.repository.CarrierRepository;
import com.conciliaciones.reconciliation.core.application.port.out.carrier.CarrierPersistencePort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CarrierPersistenceAdapter implements CarrierPersistencePort {

    private final CarrierRepository repository;

    @Override
    public CarrierEntity save(CarrierEntity entity) {
        log.info("LOG INICIO X = saveCarrierPersistence");
        CarrierEntity saved = repository.save(entity);
        log.info("LOG FIN X = saveCarrierPersistence id={}", saved.getId());
        return saved;
    }

    @Override
    public Optional<CarrierEntity> findById(Long id) {
        log.info("LOG INICIO X = findCarrierByIdPersistence id={}", id);
        Optional<CarrierEntity> result = repository.findById(id);
        log.info("LOG FIN X = findCarrierByIdPersistence found={}", result.isPresent());
        return result;
    }

    @Override
    public Page<CarrierEntity> findAll(Pageable pageable) {
        log.info("LOG INICIO X = findAllCarrierPersistence");
        Page<CarrierEntity> result = repository.findAll(pageable);
        log.info("LOG FIN X = findAllCarrierPersistence totalElements={}", result.getTotalElements());
        return result;
    }

    @Override
    public void deleteById(Long id) {
        log.info("LOG INICIO X = deleteCarrierPersistence id={}", id);
        repository.deleteById(id);
        log.info("LOG FIN X = deleteCarrierPersistence id={}", id);
    }
}
