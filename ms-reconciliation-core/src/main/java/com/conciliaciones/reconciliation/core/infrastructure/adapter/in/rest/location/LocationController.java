package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.location;

import com.conciliaciones.reconciliation.core.application.port.in.location.LocationQueryUseCase;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.location.CityResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.location.CountryResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.location.StateResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@Tag(name = "Locations", description = "Consulta de países, estados y ciudades")
public class LocationController {

    private final LocationQueryUseCase locationQueryUseCase;

    @GetMapping("/countries")
    public List<CountryResponse> countries() {
        return locationQueryUseCase.findCountries();
    }

    @GetMapping("/states")
    public List<StateResponse> states(@RequestParam Long countryId) {
        return locationQueryUseCase.findStatesByCountry(countryId);
    }

    @GetMapping("/cities/search")
    public Page<CityResponse> cities(
            @RequestParam Long stateId,
            @RequestParam(required = false) String name,
            Pageable pageable
    ) {
        return locationQueryUseCase.searchCities(stateId, name, pageable);
    }
}