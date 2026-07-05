package com.conciliaciones.reconciliation.core.application.port.in.securityMenu;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityMenu.SecurityMenuTreeResponse;

import java.util.List;

public interface ListUserSecurityMenuTreeUseCase {
    List<SecurityMenuTreeResponse> listUserMenuTree(String username);
}