package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.SecurityUserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SecurityUserRepository extends JpaRepository<SecurityUserEntity, Long> {

    Optional<SecurityUserEntity> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    Page<SecurityUserEntity> findByActive(Boolean active, Pageable pageable);
}