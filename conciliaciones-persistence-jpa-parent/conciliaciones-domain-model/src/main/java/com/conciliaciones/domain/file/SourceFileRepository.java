package com.conciliaciones.domain.file;

import java.util.List;
import java.util.Optional;

public interface SourceFileRepository {

    SourceFile save(SourceFile sourceFile);

    Optional<SourceFile> findById(Long id);

    Optional<SourceFile> findByChecksum(String checksum);

    List<SourceFile> findByClientId(Long clientId);
}
