package com.conciliaciones.msfilemanagement.application.port.in;

import com.conciliaciones.domain.file.SourceFile;

import java.util.Optional;

public interface GetSourceFileUseCase {

    Optional<SourceFile> findById(Long id);
}
