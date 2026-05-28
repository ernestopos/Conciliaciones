package com.conciliaciones.msfilemanagement.application.usecase.storage;

import com.conciliaciones.domain.file.SourceFile;
import com.conciliaciones.domain.processing.ProcessingStatus;
import com.conciliaciones.msfilemanagement.application.port.in.storage.ConfirmUploadCallbackCommand;
import com.conciliaciones.msfilemanagement.application.port.in.storage.ConfirmUploadCallbackResult;
import com.conciliaciones.msfilemanagement.application.port.in.storage.ConfirmUploadCallbackUseCase;
import com.conciliaciones.msfilemanagement.application.port.out.FileUploadConfirmedEventPublisher;
import com.conciliaciones.msfilemanagement.application.port.out.SourceFilePersistencePort;
import com.conciliaciones.msfilemanagement.infrastructure.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfirmUploadCallbackService implements ConfirmUploadCallbackUseCase {

    private static final String DEFAULT_USER = "SYSTEM";

    private final SourceFilePersistencePort sourceFilePersistencePort;
    private final FileUploadConfirmedEventPublisher fileUploadConfirmedEventPublisher;

    @Override
    public ConfirmUploadCallbackResult confirm(ConfirmUploadCallbackCommand command) {
        log.info("LOG INICIO X = confirm - sourceFileId={}, success={}", command.sourceFileId(), command.success());

        if (command.sourceFileId() == null) {
            throw new BusinessException("El sourceFileId es obligatorio para confirmar el cargue del archivo");
        }

        ProcessingStatus newStatus = command.success()
                ? ProcessingStatus.S3_UPLOAD
                : ProcessingStatus.ERROR_S3_UPLOAD;

        SourceFile updated = sourceFilePersistencePort.updateStatus(
                command.sourceFileId(),
                newStatus,
                command.errorMessage(),
                DEFAULT_USER
        );

        if (command.success()) {
            fileUploadConfirmedEventPublisher.publish(updated);
        }

        return new ConfirmUploadCallbackResult(updated.getId(), newStatus.getId(), newStatus.name());
    }
}
