package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<ClientEntity, Long> {
}
