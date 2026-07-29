package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.CarrierPortalEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarrierPortalRepository extends JpaRepository<CarrierPortalEntity, Long> {

    Optional<CarrierPortalEntity> findByCode(String code);

    Optional<CarrierPortalEntity> findByCodeAndActiveTrue(String code);

    boolean existsByCode(String code);

    @EntityGraph(attributePaths = {"carrier"})
    List<CarrierPortalEntity> findByActiveTrueOrderBySortOrderAscDisplayNameAsc();
}
