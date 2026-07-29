package com.conciliaciones.mssecurity.application.port.in;

import com.conciliaciones.mssecurity.domain.model.SecureBrowserLaunchCommand;
import com.conciliaciones.mssecurity.domain.model.SecureBrowserLaunchResult;

public interface LaunchSecureBrowserUseCase {

    SecureBrowserLaunchResult launch(Long carrierPortalId, SecureBrowserLaunchCommand command);
}