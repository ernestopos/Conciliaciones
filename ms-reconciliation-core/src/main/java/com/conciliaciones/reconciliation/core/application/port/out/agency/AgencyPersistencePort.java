package com.conciliaciones.reconciliation.core.application.port.out.agency;

import com.conciliaciones.domain.entity.AgencyEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AgencyPersistencePort {
    AgencyEntity save(AgencyEntity entity);
    Optional<AgencyEntity> findById(Long id);
    Page<AgencyEntity> findAll(Pageable pageable);
    void deleteById(Long id);
}
