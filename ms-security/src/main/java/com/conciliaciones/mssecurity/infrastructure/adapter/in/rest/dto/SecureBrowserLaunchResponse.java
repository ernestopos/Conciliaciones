package com.conciliaciones.mssecurity.infrastructure.adapter.in.rest.dto;

public record SecureBrowserLaunchResponse(
        Long carrierPortalId,
        String carrierCode,
        String carrierName,
        String portalCode,
        String portalName,
        String launchUrl,
        String auditReference
) {
}