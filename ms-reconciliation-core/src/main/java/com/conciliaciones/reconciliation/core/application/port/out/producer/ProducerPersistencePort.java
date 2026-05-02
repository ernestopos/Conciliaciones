package com.conciliaciones.reconciliation.core.application.port.out.producer;

import com.conciliaciones.domain.entity.ProducerEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProducerPersistencePort {
    ProducerEntity save(ProducerEntity entity);
    Optional<ProducerEntity> findById(Long id);
    Page<ProducerEntity> findAll(Pageable pageable);
    void deleteById(Long id);
}
