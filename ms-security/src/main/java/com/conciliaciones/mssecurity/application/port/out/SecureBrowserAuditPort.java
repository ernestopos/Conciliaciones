package com.conciliaciones.mssecurity.application.port.out;

import com.conciliaciones.domain.entity.CarrierPortalEntity;
import com.conciliaciones.mssecurity.domain.model.SecureBrowserLaunchCommand;

public interface SecureBrowserAuditPort {

    void registerRequested(
            String auditReference,
            CarrierPortalEntity portal,
            SecureBrowserLaunchCommand command
    );

    void registerAuthorized(
            String auditReference,
            CarrierPortalEntity portal,
            SecureBrowserLaunchCommand command,
            String launchUrl
    );

    void registerError(
            String auditReference,
            CarrierPortalEntity portal,
            SecureBrowserLaunchCommand command,
            String errorMessage
    );
}
