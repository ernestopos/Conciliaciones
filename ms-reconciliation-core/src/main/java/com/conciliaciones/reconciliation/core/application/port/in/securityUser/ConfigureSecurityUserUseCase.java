package com.conciliaciones.reconciliation.core.application.port.in.securityUser;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityUser.ConfigureSecurityUserRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityUser.SecurityUserResponse;

public interface ConfigureSecurityUserUseCase {

    SecurityUserResponse configure(ConfigureSecurityUserRequest request, String username);
}