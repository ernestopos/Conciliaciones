package com.conciliaciones.mssecurity.application.usecase;

import com.conciliaciones.mssecurity.application.port.in.ListKeycloakUsersUseCase;
import com.conciliaciones.mssecurity.application.port.out.KeycloakPort;
import com.conciliaciones.mssecurity.infrastructure.adapter.in.rest.dto.KeycloakUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements ListKeycloakUsersUseCase {

    private final KeycloakPort keycloakPort;

    @Override
    public List<KeycloakUserResponse> findUsers() {
        return keycloakPort.findUsers();
    }
}
