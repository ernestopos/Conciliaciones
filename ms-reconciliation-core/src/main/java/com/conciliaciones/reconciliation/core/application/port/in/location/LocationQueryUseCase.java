package com.conciliaciones.reconciliation.core.application.port.in.location;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.location.CityResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.location.CountryResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.location.StateResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LocationQueryUseCase {
    List<CountryResponse> findCountries();
    List<StateResponse> findStatesByCountry(Long countryId);
    Page<CityResponse> searchCities(Long stateId, String name, Pageable pageable);
}