package com.conciliaciones.mssecurity.infrastructure.adapter.out.audit;

import com.conciliaciones.domain.entity.CarrierPortalEntity;
import com.conciliaciones.mssecurity.application.port.in.AuditUseCase;
import com.conciliaciones.mssecurity.application.port.out.SecureBrowserAuditPort;
import com.conciliaciones.mssecurity.domain.model.AuditActionResult;
import com.conciliaciones.mssecurity.domain.model.SecureBrowserLaunchCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecureBrowserAuditAdapter implements SecureBrowserAuditPort {

    private static final String ACTION_SECURE_BROWSER_LAUNCH = "SECURE_BROWSER_LAUNCH";
    private static final String STATUS_REQUESTED = "REQUESTED";
    private static final String STATUS_AUTHORIZED = "AUTHORIZED";
    private static final String STATUS_ERROR = "ERROR";
    private final AuditUseCase auditUseCase;

    @Override
    public void registerRequested(String auditReference,CarrierPortalEntity portal,SecureBrowserLaunchCommand command) {
        log.info(
                "LOG INICIO X = registerSecureBrowserRequested auditReference={}",
                auditReference
        );

        auditUseCase.register(
                command.username(),
                ACTION_SECURE_BROWSER_LAUNCH,
                AuditActionResult.SUCCESS,
                "Solicitud de acceso al portal seguro de aseguradoras",
                null,
                buildAuditData(
                        auditReference,
                        portal,
                        command,
                        null,
                        null
                ),
                STATUS_REQUESTED
        );

        log.info(
                "LOG FIN X = registerSecureBrowserRequested auditReference={}",
                auditReference
        );
    }

    @Override
    public void registerAuthorized(
            String auditReference,
            CarrierPortalEntity portal,
            SecureBrowserLaunchCommand command,
            String launchUrl
    ) {
        log.info(
                "LOG INICIO X = registerSecureBrowserAuthorized auditReference={}",
                auditReference
        );

        auditUseCase.register(
                command.username(),
                ACTION_SECURE_BROWSER_LAUNCH,
                AuditActionResult.SUCCESS,
                "Acceso autorizado al portal seguro de aseguradoras",
                null,
                buildAuditData(
                        auditReference,
                        portal,
                        command,
                        launchUrl,
                        null
                ),
                STATUS_AUTHORIZED
        );

        log.info(
                "LOG FIN X = registerSecureBrowserAuthorized auditReference={}",
                auditReference
        );
    }

    @Override
    public void registerError(
            String auditReference,
            CarrierPortalEntity portal,
            SecureBrowserLaunchCommand command,
            String errorMessage
    ) {
        log.info(
                "LOG INICIO X = registerSecureBrowserError auditReference={}",
                auditReference
        );

        auditUseCase.register(
                command != null ? command.username() : "UNKNOWN",
                ACTION_SECURE_BROWSER_LAUNCH,
                AuditActionResult.ERROR,
                "Error iniciando la conexión con el portal seguro de aseguradoras",
                null,
                buildAuditData(
                        auditReference,
                        portal,
                        command,
                        null,
                        errorMessage
                ),
                STATUS_ERROR
        );

        log.info(
                "LOG FIN X = registerSecureBrowserError auditReference={}",
                auditReference
        );
    }

    private Map<String, Object> buildAuditData(
            String auditReference,
            CarrierPortalEntity portal,
            SecureBrowserLaunchCommand command,
            String launchUrl,
            String errorMessage
    ) {
        Map<String, Object> data = new LinkedHashMap<>();

        data.put("auditReference", auditReference);

        if (command != null) {
            data.put("userId", command.userId());
            data.put("username", command.username());
            data.put("email", command.email());
            data.put("sourceIp", command.sourceIp());
            data.put("userAgent", command.userAgent());
        }

        if (portal != null) {
            data.put("carrierPortalId", portal.getId());
            data.put("portalCode", portal.getCode());
            data.put("portalName", portal.getDisplayName());

            if (portal.getCarrier() != null) {
                data.put(
                        "carrierId",
                        portal.getCarrier().getId()
                );
                data.put(
                        "carrierCode",
                        portal.getCarrier().getCode()
                );
                data.put(
                        "carrierName",
                        portal.getCarrier().getName()
                );
            }
        }

        /*
         * Mientras launchUrl no contenga tokens ni datos sensibles
         * puede registrarse. Si posteriormente incorpora información
         * temporal o firmada, se debe omitir o enmascarar.
         */
        if (launchUrl != null && !launchUrl.isBlank()) {
            data.put("launchUrl", launchUrl);
        }

        if (errorMessage != null && !errorMessage.isBlank()) {
            data.put("errorMessage", errorMessage);
        }

        return data;
    }
}