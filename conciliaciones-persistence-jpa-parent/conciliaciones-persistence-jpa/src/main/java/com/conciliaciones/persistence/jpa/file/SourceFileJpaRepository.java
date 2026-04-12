package com.conciliaciones.persistence.jpa.file;

import java.util.List;
import java.util.Optional;

import com.conciliaciones.persistence.jpa.entity.SourceFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SourceFileJpaRepository extends JpaRepository<SourceFileEntity, Long> {

    Optional<SourceFileEntity> findByChecksum(String checksum);
}
