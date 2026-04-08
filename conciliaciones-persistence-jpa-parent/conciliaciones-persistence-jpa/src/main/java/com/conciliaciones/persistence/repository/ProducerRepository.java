package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.ProducerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProducerRepository extends JpaRepository<ProducerEntity, Long> {
}
