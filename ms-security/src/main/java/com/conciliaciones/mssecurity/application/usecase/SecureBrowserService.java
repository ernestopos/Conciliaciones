package com.conciliaciones.mssecurity.application.usecase;

import com.conciliaciones.domain.entity.CarrierEntity;
import com.conciliaciones.domain.entity.CarrierPortalEntity;
import com.conciliaciones.mssecurity.application.port.in.LaunchSecureBrowserUseCase;
import com.conciliaciones.mssecurity.application.port.out.CarrierPortalQueryPort;
import com.conciliaciones.mssecurity.application.port.out.SecureBrowserAuditPort;
import com.conciliaciones.mssecurity.application.port.out.SecureBrowserLaunchUrlPort;
import com.conciliaciones.mssecurity.domain.exception.CarrierPortalNotFoundException;
import com.conciliaciones.mssecurity.domain.exception.SecureBrowserLaunchException;
import com.conciliaciones.mssecurity.domain.model.SecureBrowserLaunchCommand;
import com.conciliaciones.mssecurity.domain.model.SecureBrowserLaunchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecureBrowserService implements LaunchSecureBrowserUseCase {

    private final CarrierPortalQueryPort carrierPortalQueryPort;
    private final SecureBrowserLaunchUrlPort launchUrlPort;
    private final SecureBrowserAuditPort auditPort;

    @Override
    @Transactional
    public SecureBrowserLaunchResult launch(Long carrierPortalId,SecureBrowserLaunchCommand command) {
        log.info("Inicio lanzamiento Secure Browser. portalId={}, username={}",carrierPortalId,command.username());

        CarrierPortalEntity portal = carrierPortalQueryPort
                .findActiveById(carrierPortalId)
                .orElseThrow(() -> new CarrierPortalNotFoundException(
                        "No existe un portal activo con id: " + carrierPortalId
                ));

        validatePortal(portal);
        CarrierEntity carrier = portal.getCarrier();
        String auditReference = UUID.randomUUID().toString();
        try {
            auditPort.registerRequested(auditReference,portal,command);
            String launchUrl = launchUrlPort.build(portal.getPortalUrl());
            auditPort.registerAuthorized(auditReference,portal,command,launchUrl);
            log.info("URL de lanzamiento generada. portalId={}, auditReference={}",portal.getId(),auditReference);
            return new SecureBrowserLaunchResult(portal.getId(),carrier != null ? carrier.getCode() : null,carrier != null ? carrier.getName() : null,portal.getCode(),portal.getDisplayName(),launchUrl,auditReference);
        } catch (CarrierPortalNotFoundException exception) {
            throw exception;

        } catch (Exception exception) {
            log.error(
                    "Error generando lanzamiento Secure Browser. portalId={}, auditReference={}",
                    carrierPortalId,
                    auditReference,
                    exception
            );

            auditPort.registerError(
                    auditReference,
                    portal,
                    command,
                    exception.getMessage()
            );

            throw new SecureBrowserLaunchException(
                    "No fue posible iniciar la conexión segura",
                    exception
            );
        }
    }

    private void validatePortal(CarrierPortalEntity portal) {
        if (!Boolean.TRUE.equals(portal.getActive())) {
            throw new CarrierPortalNotFoundException(
                    "El portal seleccionado se encuentra inactivo"
            );
        }

        if (portal.getPortalUrl() == null || portal.getPortalUrl().isBlank()) {
            throw new SecureBrowserLaunchException(
                    "El portal seleccionado no tiene una URL configurada"
            );
        }

        if (!isValidHttpsUrl(portal.getPortalUrl())) {
            throw new SecureBrowserLaunchException(
                    "La URL del portal seleccionado no es válida o no utiliza HTTPS"
            );
        }
    }

    private boolean isValidHttpsUrl(String url) {
        return url.startsWith("https://");
    }
}
