package com.conciliaciones.mssecurity.infrastructure.adapter.in.rest;

import com.conciliaciones.mssecurity.application.port.in.LaunchSecureBrowserUseCase;
import com.conciliaciones.mssecurity.domain.model.SecureBrowserLaunchCommand;
import com.conciliaciones.mssecurity.domain.model.SecureBrowserLaunchResult;
import com.conciliaciones.mssecurity.infrastructure.adapter.in.rest.dto.SecureBrowserLaunchRequest;
import com.conciliaciones.mssecurity.infrastructure.adapter.in.rest.dto.SecureBrowserLaunchResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/secure-browser")
public class SecureBrowserController {

    private final LaunchSecureBrowserUseCase launchSecureBrowserUseCase;

    @PostMapping("/launch")
    public ResponseEntity<SecureBrowserLaunchResponse> launch(
            @Valid @RequestBody SecureBrowserLaunchRequest request,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest httpRequest
    ) {
        String userId = jwt.getSubject();
        String username = firstNonBlank(jwt.getClaimAsString("preferred_username"),jwt.getClaimAsString("email"),jwt.getSubject());
        String email = jwt.getClaimAsString("email");
        String sourceIp = resolveClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        log.info("Solicitud Secure Browser. portalId={}, username={}, sourceIp={}",request.carrierPortalId(),username,sourceIp);
        SecureBrowserLaunchCommand command = new SecureBrowserLaunchCommand(userId,username,email,sourceIp,userAgent);
        SecureBrowserLaunchResult result = launchSecureBrowserUseCase.launch(request.carrierPortalId(),command);
        SecureBrowserLaunchResponse response =
                new SecureBrowserLaunchResponse(
                        result.carrierPortalId(),
                        result.carrierCode(),
                        result.carrierName(),
                        result.portalCode(),
                        result.portalName(),
                        result.launchUrl(),
                        result.auditReference()
                );
        return ResponseEntity.ok(response);
    }

    private String resolveClientIp(HttpServletRequest request) {
        /*
         * El ALB coloca la IP original en X-Forwarded-For.
         * Puede contener varias IP separadas por coma.
         */
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "unknown";
    }
}
