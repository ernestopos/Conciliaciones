package com.conciliaciones.reconciliation.core.application.usecase.location;

import com.conciliaciones.domain.entity.CityEntity;
import com.conciliaciones.domain.entity.CountryEntity;
import com.conciliaciones.domain.entity.StateEntity;
import com.conciliaciones.reconciliation.core.application.port.in.location.LocationQueryUseCase;
import com.conciliaciones.reconciliation.core.application.port.out.location.CityPersistencePort;
import com.conciliaciones.reconciliation.core.application.port.out.location.CountryPersistencePort;
import com.conciliaciones.reconciliation.core.application.port.out.location.StatePersistencePort;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.location.CityResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.location.CountryResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.location.StateResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationService implements LocationQueryUseCase {

    private final CountryPersistencePort countryPersistencePort;
    private final StatePersistencePort statePersistencePort;
    private final CityPersistencePort cityPersistencePort;

    @Override
    public List<CountryResponse> findCountries() {
        return countryPersistencePort.findActive()
                .stream()
                .map(this::toCountryResponse)
                .toList();
    }

    @Override
    public List<StateResponse> findStatesByCountry(Long countryId) {
        return statePersistencePort.findActiveByCountryId(countryId)
                .stream()
                .map(this::toStateResponse)
                .toList();
    }

    @Override
    public Page<CityResponse> searchCities(Long stateId, String name, Pageable pageable) {
        return cityPersistencePort.searchByState(stateId, name, pageable)
                .map(this::toCityResponse);
    }

    private CountryResponse toCountryResponse(CountryEntity entity) {
        return new CountryResponse(
                entity.getId(),
                entity.getCode(),
                entity.getName()
        );
    }

    private StateResponse toStateResponse(StateEntity entity) {
        CountryEntity country = entity.getCountry();

        return new StateResponse(
                entity.getId(),
                country != null ? country.getId() : null,
                entity.getCode(),
                entity.getName()
        );
    }

    private CityResponse toCityResponse(CityEntity entity) {
        StateEntity state = entity.getState();
        CountryEntity country = state != null ? state.getCountry() : null;

        return new CityResponse(
                entity.getId(),
                entity.getName(),
                state != null ? state.getId() : null,
                state != null ? state.getName() : null,
                country != null ? country.getId() : null,
                country != null ? country.getName() : null
        );
    }
}