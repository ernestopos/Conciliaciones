package com.conciliaciones.msfilemanagement.infrastructure.adapter.out.persistence;

import com.conciliaciones.domain.file.SourceFile;
import com.conciliaciones.domain.file.SourceFileRepository;
import com.conciliaciones.msfilemanagement.application.port.out.SourceFilePersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SharedSourceFilePersistenceAdapter implements SourceFilePersistencePort {

    private final SourceFileRepository sourceFileRepository;

    @Override
    public SourceFile save(SourceFile sourceFile) {
        return sourceFileRepository.save(sourceFile);
    }

    @Override
    public Optional<SourceFile> findById(Long id) {
        return sourceFileRepository.findById(id);
    }

    @Override
    public Optional<SourceFile> findByChecksum(String checksum) {
        return sourceFileRepository.findByChecksum(checksum);
    }
}
