package com.conciliaciones.mssecurity.application.port.in;

import com.conciliaciones.mssecurity.infrastructure.adapter.in.rest.dto.KeycloakUserResponse;

import java.util.List;

public interface ListKeycloakUsersUseCase {

    List<KeycloakUserResponse> findUsers();
}