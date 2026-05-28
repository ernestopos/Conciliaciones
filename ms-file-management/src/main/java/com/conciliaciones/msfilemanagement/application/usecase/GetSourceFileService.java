package com.conciliaciones.msfilemanagement.application.usecase;

import com.conciliaciones.domain.file.SourceFile;
import com.conciliaciones.msfilemanagement.application.port.in.GetSourceFileUseCase;
import com.conciliaciones.msfilemanagement.application.port.out.SourceFilePersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetSourceFileService implements GetSourceFileUseCase {

    private final SourceFilePersistencePort sourceFilePersistencePort;

    @Override
    public Optional<SourceFile> findById(Long id) {
        return sourceFilePersistencePort.findById(id);
    }
}
