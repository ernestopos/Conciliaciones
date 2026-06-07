package com.conciliaciones.reconciliation.core.infrastructure.adapter.out.persistence.location;

import com.conciliaciones.domain.entity.CountryEntity;
import com.conciliaciones.persistence.repository.CountryRepository;
import com.conciliaciones.reconciliation.core.application.port.out.location.CountryPersistencePort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CountryPersistenceAdapter implements CountryPersistencePort {

    private final CountryRepository repository;

    @Override
    public List<CountryEntity> findActive() {
        return repository.findByActiveTrueOrderByNameAsc();
    }
}