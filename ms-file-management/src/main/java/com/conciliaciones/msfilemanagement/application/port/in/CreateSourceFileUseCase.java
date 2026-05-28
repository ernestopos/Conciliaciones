package com.conciliaciones.msfilemanagement.application.port.in;

import com.conciliaciones.domain.file.SourceFile;

public interface CreateSourceFileUseCase {

    SourceFile create(CreateSourceFileCommand command);
}
