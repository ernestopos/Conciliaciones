package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.StateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StateRepository extends JpaRepository<StateEntity, Long> {

    List<StateEntity> findByCountryIdAndActiveTrueOrderByNameAsc(Long countryId);
}