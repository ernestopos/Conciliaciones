package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.CityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<CityEntity, Long> {

    Page<CityEntity> findByStateIdAndNameContainingIgnoreCaseAndActiveTrueOrderByNameAsc(
            Long stateId,
            String name,
            Pageable pageable
    );
}