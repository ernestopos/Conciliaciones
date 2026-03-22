package com.conciliaciones.persistence.adapter;

import com.conciliaciones.domain.file.SourceFile;
import com.conciliaciones.domain.file.SourceFileRepository;
import com.conciliaciones.persistence.jpa.file.SourceFileJpaRepository;
import com.conciliaciones.persistence.mapper.SourceFilePersistenceMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SourceFileRepositoryAdapter implements SourceFileRepository {

    private final SourceFileJpaRepository sourceFileJpaRepository;
    private final SourceFilePersistenceMapper mapper;

    @Override
    public SourceFile save(SourceFile sourceFile) {
        return mapper.toDomain(sourceFileJpaRepository.save(mapper.toEntity(sourceFile)));
    }

    @Override
    public Optional<SourceFile> findById(Long id) {
        return sourceFileJpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<SourceFile> findByChecksum(String checksum) {
        return sourceFileJpaRepository.findByChecksum(checksum).map(mapper::toDomain);
    }

    @Override
    public List<SourceFile> findByClientId(Long clientId) {
        return sourceFileJpaRepository.findByClientId(clientId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
