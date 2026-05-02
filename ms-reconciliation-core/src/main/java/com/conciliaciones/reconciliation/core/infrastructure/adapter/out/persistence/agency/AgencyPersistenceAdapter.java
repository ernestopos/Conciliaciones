package com.conciliaciones.reconciliation.core.infrastructure.adapter.out.persistence.agency;

import com.conciliaciones.domain.entity.AgencyEntity;
import com.conciliaciones.persistence.repository.AgencyRepository;
import com.conciliaciones.reconciliation.core.application.port.out.agency.AgencyPersistencePort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AgencyPersistenceAdapter implements AgencyPersistencePort {

    private final AgencyRepository repository;

    @Override
    public AgencyEntity save(AgencyEntity entity) {
        log.info("LOG INICIO X = saveAgencyPersistence");
        AgencyEntity saved = repository.save(entity);
        log.info("LOG FIN X = saveAgencyPersistence id={}", saved.getId());
        return saved;
    }

    @Override
    public Optional<AgencyEntity> findById(Long id) {
        log.info("LOG INICIO X = findAgencyByIdPersistence id={}", id);
        Optional<AgencyEntity> result = repository.findById(id);
        log.info("LOG FIN X = findAgencyByIdPersistence found={}", result.isPresent());
        return result;
    }

    @Override
    public Page<AgencyEntity> findAll(Pageable pageable) {
        log.info("LOG INICIO X = findAllAgencyPersistence");
        Page<AgencyEntity> result = repository.findAll(pageable);
        log.info("LOG FIN X = findAllAgencyPersistence totalElements={}", result.getTotalElements());
        return result;
    }

    @Override
    public void deleteById(Long id) {
        log.info("LOG INICIO X = deleteAgencyPersistence id={}", id);
        repository.deleteById(id);
        log.info("LOG FIN X = deleteAgencyPersistence id={}", id);
    }
}
