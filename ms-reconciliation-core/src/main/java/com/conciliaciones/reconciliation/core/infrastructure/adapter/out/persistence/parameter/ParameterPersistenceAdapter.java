package com.conciliaciones.reconciliation.core.infrastructure.adapter.out.persistence.parameter;

import com.conciliaciones.domain.entity.ParameterEntity;
import com.conciliaciones.persistence.repository.ParameterRepository;
import com.conciliaciones.reconciliation.core.application.port.out.parameter.ParameterPersistencePort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ParameterPersistenceAdapter implements ParameterPersistencePort {

    private final ParameterRepository repository;

    @Override
    public ParameterEntity save(ParameterEntity entity) {
        log.info("LOG INICIO X = saveParameterPersistence");
        ParameterEntity saved = repository.save(entity);
        log.info("LOG FIN X = saveParameterPersistence id={}", saved.getId());
        return saved;
    }

    @Override
    public Optional<ParameterEntity> findById(Long id) {
        log.info("LOG INICIO X = findParameterByIdPersistence id={}", id);
        Optional<ParameterEntity> result = repository.findById(id);
        log.info("LOG FIN X = findParameterByIdPersistence found={}", result.isPresent());
        return result;
    }

    @Override
    public Page<ParameterEntity> findAll(Pageable pageable) {
        log.info("LOG INICIO X = findAllParameterPersistence");
        Page<ParameterEntity> result = repository.findAll(pageable);
        log.info("LOG FIN X = findAllParameterPersistence totalElements={}", result.getTotalElements());
        return result;
    }

    @Override
    public void deleteById(Long id) {
        log.info("LOG INICIO X = deleteParameterPersistence id={}", id);
        repository.deleteById(id);
        log.info("LOG FIN X = deleteParameterPersistence id={}", id);
    }
}
