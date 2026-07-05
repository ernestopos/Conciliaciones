package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.SecuritySubMenuEntity;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SecuritySubMenuRepository extends JpaRepository<SecuritySubMenuEntity, Long> {

    @EntityGraph(attributePaths = {"parameter", "menu"})
    List<SecuritySubMenuEntity> findByMenuIdOrderBySortOrderAsc(Long menuId);

    @EntityGraph(attributePaths = {"parameter", "menu"})
    List<SecuritySubMenuEntity> findByMenuIdAndActiveOrderBySortOrderAsc(Long menuId, Boolean active);

    @EntityGraph(attributePaths = {"parameter", "menu"})
    List<SecuritySubMenuEntity> findByMenuIdInOrderByMenuSortOrderAscSortOrderAsc(List<Long> menuIds);

    @EntityGraph(attributePaths = {"parameter", "menu"})
    List<SecuritySubMenuEntity> findByMenuIdInAndActiveOrderByMenuSortOrderAscSortOrderAsc(List<Long> menuIds, Boolean active);

    @Query("""
        SELECT sm
          FROM SecuritySubMenuEntity sm
          JOIN FETCH sm.menu m
         WHERE sm.id IN :ids
           AND sm.active = true
           AND m.active = true
    """)
    List<SecuritySubMenuEntity> findActiveByIdInWithMenu(@Param("ids") List<Long> ids);
}
