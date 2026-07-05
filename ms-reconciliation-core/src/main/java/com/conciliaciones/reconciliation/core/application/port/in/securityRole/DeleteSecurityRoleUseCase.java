package com.conciliaciones.reconciliation.core.application.port.in.securityRole;

public interface DeleteSecurityRoleUseCase {
    void delete(Long id, String username);
}
