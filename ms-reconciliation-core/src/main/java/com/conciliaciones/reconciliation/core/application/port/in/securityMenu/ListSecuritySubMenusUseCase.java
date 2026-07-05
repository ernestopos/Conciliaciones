package com.conciliaciones.reconciliation.core.application.port.in.securityMenu;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityMenu.SecuritySubMenuResponse;
import java.util.List;

public interface ListSecuritySubMenusUseCase {
    List<SecuritySubMenuResponse> listByMenuId(Long menuId, Boolean active);
}
