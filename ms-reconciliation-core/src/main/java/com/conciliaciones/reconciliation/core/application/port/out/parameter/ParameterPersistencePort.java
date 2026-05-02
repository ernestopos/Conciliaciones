package com.conciliaciones.reconciliation.core.application.port.out.parameter;

import com.conciliaciones.domain.entity.ParameterEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ParameterPersistencePort {
    ParameterEntity save(ParameterEntity entity);
    Optional<ParameterEntity> findById(Long id);
    Page<ParameterEntity> findAll(Pageable pageable);
    void deleteById(Long id);
}
