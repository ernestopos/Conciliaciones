package com.conciliaciones.reconciliation.core.application.port.in.securityUser;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityUser.SecurityUserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListConfiguredSecurityUsersUseCase {

    Page<SecurityUserResponse> list(Boolean active, Pageable pageable);
}