package com.conciliaciones.msfilemanagement.application.port.out;

import com.conciliaciones.domain.file.SourceFile;

public interface FileUploadConfirmedEventPublisher {
    void publish(SourceFile sourceFile);
}
