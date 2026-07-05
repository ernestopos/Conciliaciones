package com.conciliaciones.reconciliation.core.application.port.in.securityUserRole;

public interface DeleteSecurityUserRoleUseCase {
    void deleteByUserId(Long userId, String username);
}