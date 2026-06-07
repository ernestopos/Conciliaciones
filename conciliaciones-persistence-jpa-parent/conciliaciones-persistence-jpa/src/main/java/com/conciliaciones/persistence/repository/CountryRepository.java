package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryRepository extends JpaRepository<CountryEntity, Long> {

    List<CountryEntity> findByActiveTrueOrderByNameAsc();
}