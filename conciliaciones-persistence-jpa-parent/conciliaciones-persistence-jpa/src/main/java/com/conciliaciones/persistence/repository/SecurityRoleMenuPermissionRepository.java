package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.SecurityRoleMenuPermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SecurityRoleMenuPermissionRepository extends JpaRepository<SecurityRoleMenuPermissionEntity, Long> {

    /**
     * Obtiene todos los permisos asociados a un rol.
     *
     * @param roleId Identificador del rol.
     * @return Lista de permisos.
     */
    List<SecurityRoleMenuPermissionEntity> findByRoleId(Long roleId);

    /**
     * Obtiene los permisos activos asociados a un rol.
     *
     * @param roleId Identificador del rol.
     * @return Lista de permisos activos.
     */
    List<SecurityRoleMenuPermissionEntity> findByRoleIdAndActiveTrue(Long roleId);

    /**
     * Obtiene los permisos activos ordenados por menú y submenú.
     *
     * @param roleId Identificador del rol.
     * @return Lista ordenada de permisos.
     */
    List<SecurityRoleMenuPermissionEntity>
    findByRoleIdAndActiveTrueOrderByMenuSortOrderAscSubMenuSortOrderAsc(Long roleId);

    /**
     * Obtiene todos los permisos ordenados por menú y submenú.
     *
     * @param roleId Identificador del rol.
     * @return Lista ordenada de permisos.
     */
    List<SecurityRoleMenuPermissionEntity>
    findByRoleIdOrderByMenuSortOrderAscSubMenuSortOrderAsc(Long roleId);

    /**
     * Elimina todos los permisos asociados a un rol.
     *
     * @param roleId Identificador del rol.
     */
    @Modifying(flushAutomatically = true)
    @Query("""
        DELETE FROM SecurityRoleMenuPermissionEntity p
         WHERE p.role.id = :roleId
    """)
    void deleteByRoleId(@Param("roleId") Long roleId);

    @Query("""
        SELECT p
          FROM SecurityRoleMenuPermissionEntity p
          JOIN FETCH p.role r
          JOIN FETCH p.menu m
          LEFT JOIN FETCH p.subMenu sm
         WHERE LOWER(r.code) = LOWER(:roleCode)
           AND r.active = true
           AND p.active = true
           AND m.active = true
           AND sm.active = true
         ORDER BY m.sortOrder ASC, sm.sortOrder ASC
    """)
    List<SecurityRoleMenuPermissionEntity> findActivePermissionsByRoleCode(@Param("roleCode") String roleCode);

    @Query("""
        SELECT p
          FROM SecurityRoleMenuPermissionEntity p
          JOIN FETCH p.role r
          JOIN FETCH p.menu m
          LEFT JOIN FETCH p.subMenu sm
         WHERE r.id = :roleId
           AND r.active = true
           AND p.active = true
           AND m.active = true
           AND sm.active = true
         ORDER BY m.sortOrder ASC, sm.sortOrder ASC
    """)
    List<SecurityRoleMenuPermissionEntity> findActivePermissionsByRoleId(@Param("roleId") Long roleId);
}