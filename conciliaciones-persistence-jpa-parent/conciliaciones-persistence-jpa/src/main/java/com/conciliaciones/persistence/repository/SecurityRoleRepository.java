package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.SecurityRoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SecurityRoleRepository extends JpaRepository<SecurityRoleEntity, Long> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

    Page<SecurityRoleEntity> findByActive(Boolean active, Pageable pageable);

    Optional<SecurityRoleEntity> findByCodeIgnoreCaseAndActiveTrue(String code);
}