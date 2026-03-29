package com.conciliaciones.mssecurity.infrastructure.adapter.in.rest;

import com.conciliaciones.mssecurity.application.port.in.AuthUseCase;
import com.conciliaciones.mssecurity.domain.model.LoginResult;
import com.conciliaciones.mssecurity.domain.model.UserValidationResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(com.conciliaciones.mssecurity.infrastructure.config.SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthUseCase authUseCase;

    @Test
    void shouldLogin() throws Exception {
        when(authUseCase.login("admin-app", "AdminApp123*")).thenReturn(
                new LoginResult("token", "refresh", "Bearer", 300, List.of("ADMIN")));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "admin-app",
                                  "password": "AdminApp123*"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("token"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldValidate() throws Exception {
        when(authUseCase.validate(anyString())).thenReturn(new UserValidationResult("admin-app", true, "Token válido"));

        mockMvc.perform(get("/auth/validate")
                        .header("Authorization", "Bearer test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin-app"));
    }
}
