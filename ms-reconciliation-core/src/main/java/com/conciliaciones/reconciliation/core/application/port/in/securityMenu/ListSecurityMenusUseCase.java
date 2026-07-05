package com.conciliaciones.reconciliation.core.application.port.in.securityMenu;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityMenu.SecurityMenuResponse;
import java.util.List;

public interface ListSecurityMenusUseCase {
    List<SecurityMenuResponse> list(Boolean active);
}
