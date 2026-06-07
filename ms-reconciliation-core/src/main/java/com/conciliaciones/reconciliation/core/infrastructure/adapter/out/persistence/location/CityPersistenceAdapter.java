package com.conciliaciones.reconciliation.core.infrastructure.adapter.out.persistence.location;

import com.conciliaciones.domain.entity.CityEntity;
import com.conciliaciones.persistence.repository.CityRepository;
import com.conciliaciones.reconciliation.core.application.port.out.location.CityPersistencePort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CityPersistenceAdapter implements CityPersistencePort {

    private final CityRepository repository;

    @Override
    public Optional<CityEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Page<CityEntity> searchByState(Long stateId, String name, Pageable pageable) {
        String filter = name == null ? "" : name.trim();

        return repository.findByStateIdAndNameContainingIgnoreCaseAndActiveTrueOrderByNameAsc(
                stateId,
                filter,
                pageable
        );
    }
}