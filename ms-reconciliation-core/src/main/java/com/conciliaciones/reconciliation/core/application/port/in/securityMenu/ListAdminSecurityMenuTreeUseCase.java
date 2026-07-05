package com.conciliaciones.reconciliation.core.application.port.in.securityMenu;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityMenu.SecurityMenuTreeResponse;

import java.util.List;

public interface ListAdminSecurityMenuTreeUseCase {
    List<SecurityMenuTreeResponse> listAdminMenuTree();
}