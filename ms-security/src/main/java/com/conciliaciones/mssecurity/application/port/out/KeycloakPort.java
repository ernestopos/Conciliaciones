package com.conciliaciones.mssecurity.application.port.out;

import com.conciliaciones.mssecurity.domain.model.LoginResult;
import com.conciliaciones.mssecurity.domain.model.UserValidationResult;

public interface KeycloakPort {

    LoginResult login(String username, String password);

    UserValidationResult validateToken(String accessToken);
}
