package com.conciliaciones.mssecurity.application.usecase;

import com.conciliaciones.mssecurity.application.port.in.AuditUseCase;
import com.conciliaciones.mssecurity.application.port.out.KeycloakPort;
import com.conciliaciones.mssecurity.domain.model.LoginResult;
import com.conciliaciones.mssecurity.domain.model.UserValidationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private KeycloakPort keycloakPort;

    @Mock
    private AuditUseCase auditUseCase;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldLoginSuccessfully() {
        when(keycloakPort.login("admin-app", "AdminApp123*")).thenReturn(
                new LoginResult("access", "refresh", "Bearer", 300, List.of("ADMIN")));

        LoginResult result = authService.login("admin-app", "AdminApp123*");

        assertThat(result.accessToken()).isEqualTo("access");
        verify(auditUseCase).register(eq("admin-app"), eq("LOGIN"), any(), any(), any(), any(), any());
    }

    @Test
    void shouldValidateTokenSuccessfully() {
        when(keycloakPort.validateToken("token123")).thenReturn(new UserValidationResult("admin-app", true, "Token válido"));

        UserValidationResult result = authService.validate("Bearer token123");

        assertThat(result.active()).isTrue();
        verify(auditUseCase).register(eq("admin-app"), eq("VALIDATE_TOKEN"), any(), any(), any(), any(), any());
    }
}
