package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.SecurityMenuEntity;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityMenuRepository extends JpaRepository<SecurityMenuEntity, Long> {

    @EntityGraph(attributePaths = {"parameter"})
    List<SecurityMenuEntity> findAllByOrderBySortOrderAsc();

    @EntityGraph(attributePaths = {"parameter"})
    List<SecurityMenuEntity> findByActiveOrderBySortOrderAsc(Boolean active);
}
