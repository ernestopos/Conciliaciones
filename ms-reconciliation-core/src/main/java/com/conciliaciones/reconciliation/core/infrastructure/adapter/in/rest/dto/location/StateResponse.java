package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.location;

public record StateResponse(
        Long id,
        Long countryId,
        String code,
        String name
) {
}