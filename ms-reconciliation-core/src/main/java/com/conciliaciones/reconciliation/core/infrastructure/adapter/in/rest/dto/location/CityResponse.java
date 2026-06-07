package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.location;

public record CityResponse(
        Long id,
        String name,
        Long stateId,
        String stateName,
        Long countryId,
        String countryName
) {
}