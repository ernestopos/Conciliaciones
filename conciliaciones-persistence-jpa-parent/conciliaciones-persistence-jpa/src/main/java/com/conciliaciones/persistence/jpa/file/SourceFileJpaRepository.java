package com.conciliaciones.persistence.jpa.file;

import com.conciliaciones.persistence.jpa.entity.SourceFileEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SourceFileJpaRepository extends JpaRepository<SourceFileEntity, Long> {

    Optional<SourceFileEntity> findByChecksum(String checksum);

    List<SourceFileEntity> findByClientId(Long clientId);
}
