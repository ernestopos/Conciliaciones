package com.conciliaciones.mssecurity.domain.model;

public record SecureBrowserLaunchResult(
        Long carrierPortalId,
        String carrierCode,
        String carrierName,
        String portalCode,
        String portalName,
        String launchUrl,
        String auditReference
) {
}