package com.conciliaciones.reconciliation.core.application.port.out.location;

import com.conciliaciones.domain.entity.StateEntity;
import java.util.List;

public interface StatePersistencePort {
    List<StateEntity> findActiveByCountryId(Long countryId);
}