    package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.parameter;

    import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

    public record CreateParameterRequest(
            @NotBlank String name,
        String description,
        @NotBlank String value,
        @NotBlank String parameterGroup,
        @NotNull Boolean active,
        @NotNull Integer sortOrder
    ) {
    }
