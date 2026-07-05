package com.conciliaciones.mssecurity.application.port.out;

import com.conciliaciones.mssecurity.domain.model.LoginResult;
import com.conciliaciones.mssecurity.domain.model.UserValidationResult;
import com.conciliaciones.mssecurity.infrastructure.adapter.in.rest.dto.KeycloakUserResponse;

import java.util.List;

public interface KeycloakPort {

    LoginResult login(String username, String password);

    UserValidationResult validateToken(String accessToken);

    List<KeycloakUserResponse> findUsers();
}
