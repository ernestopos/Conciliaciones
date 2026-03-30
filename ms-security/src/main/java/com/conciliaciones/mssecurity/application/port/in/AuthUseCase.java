package com.conciliaciones.mssecurity.application.port.in;

import com.conciliaciones.mssecurity.domain.model.LoginResult;
import com.conciliaciones.mssecurity.domain.model.UserValidationResult;

import java.util.List;

public interface AuthUseCase {

    LoginResult login(String username, String password);

    UserValidationResult validate(String authorizationHeader);

    List<String> getRoles(String authorizationHeader);
}
