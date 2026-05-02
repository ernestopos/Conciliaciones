package com.conciliaciones.reconciliation.core.infrastructure.adapter.out.persistence.producer;

import com.conciliaciones.domain.entity.ProducerEntity;
import com.conciliaciones.persistence.repository.ProducerRepository;
import com.conciliaciones.reconciliation.core.application.port.out.producer.ProducerPersistencePort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProducerPersistenceAdapter implements ProducerPersistencePort {

    private final ProducerRepository repository;

    @Override
    public ProducerEntity save(ProducerEntity entity) {
        log.info("LOG INICIO X = saveProducerPersistence");
        ProducerEntity saved = repository.save(entity);
        log.info("LOG FIN X = saveProducerPersistence id={}", saved.getId());
        return saved;
    }

    @Override
    public Optional<ProducerEntity> findById(Long id) {
        log.info("LOG INICIO X = findProducerByIdPersistence id={}", id);
        Optional<ProducerEntity> result = repository.findById(id);
        log.info("LOG FIN X = findProducerByIdPersistence found={}", result.isPresent());
        return result;
    }

    @Override
    public Page<ProducerEntity> findAll(Pageable pageable) {
        log.info("LOG INICIO X = findAllProducerPersistence");
        Page<ProducerEntity> result = repository.findAll(pageable);
        log.info("LOG FIN X = findAllProducerPersistence totalElements={}", result.getTotalElements());
        return result;
    }

    @Override
    public void deleteById(Long id) {
        log.info("LOG INICIO X = deleteProducerPersistence id={}", id);
        repository.deleteById(id);
        log.info("LOG FIN X = deleteProducerPersistence id={}", id);
    }
}
