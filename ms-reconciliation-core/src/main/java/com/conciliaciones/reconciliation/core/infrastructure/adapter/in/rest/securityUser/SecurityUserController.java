package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.securityUser;

import com.conciliaciones.reconciliation.core.application.port.in.securityUser.ConfigureSecurityUserUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.securityUser.ListConfiguredSecurityUsersUseCase;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityUser.ConfigureSecurityUserRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityUser.SecurityUserResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.support.AuthenticatedUserResolver;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/core/v1/security/users")
@RequiredArgsConstructor
public class SecurityUserController {

    private final ConfigureSecurityUserUseCase configureSecurityUserUseCase;
    private final ListConfiguredSecurityUsersUseCase listConfiguredSecurityUsersUseCase;

    @PostMapping("/configure")
    public SecurityUserResponse configure(@Valid @RequestBody ConfigureSecurityUserRequest request,
                                          @AuthenticationPrincipal Jwt jwt) {
        return configureSecurityUserUseCase.configure(request, AuthenticatedUserResolver.resolveUsername(jwt));
    }

    @GetMapping("/configured")
    public Page<SecurityUserResponse> listConfigured(@RequestParam(required = false) Boolean active,
                                                     Pageable pageable) {
        return listConfiguredSecurityUsersUseCase.list(active, pageable);
    }
}