package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.SecurityUserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SecurityUserRoleRepository extends JpaRepository<SecurityUserRoleEntity, Long> {

    Optional<SecurityUserRoleEntity> findByUserId(Long userId);

    Optional<SecurityUserRoleEntity> findByUserIdAndActiveTrue(Long userId);

    List<SecurityUserRoleEntity> findByActiveTrueOrderByUserFullNameAsc();

    List<SecurityUserRoleEntity> findByActive(Boolean active);

    Optional<SecurityUserRoleEntity> findByUserUsernameIgnoreCaseAndActiveTrue(String username);
}