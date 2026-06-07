package com.conciliaciones.reconciliation.core.application.port.out.location;

import com.conciliaciones.domain.entity.CountryEntity;
import java.util.List;

public interface CountryPersistencePort {
    List<CountryEntity> findActive();
}