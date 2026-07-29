package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.carrierPortal;

public record CarrierPortalResponse(
        Long id,
        Long carrierId,
        String carrierCode,
        String carrierName,
        String code,
        String displayName,
        String portalUrl,
        String logoUrl,
        String description,
        Boolean allowUpload,
        Boolean allowDownload,
        Boolean requiresMfa
) {
}