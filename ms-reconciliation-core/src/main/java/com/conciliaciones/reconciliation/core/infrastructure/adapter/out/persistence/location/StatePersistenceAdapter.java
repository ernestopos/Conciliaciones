package com.conciliaciones.reconciliation.core.infrastructure.adapter.out.persistence.location;

import com.conciliaciones.domain.entity.StateEntity;
import com.conciliaciones.persistence.repository.StateRepository;
import com.conciliaciones.reconciliation.core.application.port.out.location.StatePersistencePort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StatePersistenceAdapter implements StatePersistencePort {

    private final StateRepository repository;

    @Override
    public List<StateEntity> findActiveByCountryId(Long countryId) {
        return repository.findByCountryIdAndActiveTrueOrderByNameAsc(countryId);
    }
}