package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.ProducerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProducerRepository extends JpaRepository<ProducerEntity, Long> {

    boolean existsByExternalProducerId(String externalProducerId);
    Optional<ProducerEntity> findByExternalProducerId(String externalProducerId);

}