package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.securityMenu;

import com.conciliaciones.reconciliation.core.application.port.in.securityMenu.*;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityMenu.SecurityMenuResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityMenu.SecurityMenuTreeResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityMenu.SecuritySubMenuResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/core/v1/security/menus")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Security Menus", description = "Consulta de menús y submenús de seguridad")
public class SecurityMenuController {

    private final ListSecurityMenuTreeUseCase listSecurityMenuTreeUseCase;
    private final ListSecurityMenusUseCase listSecurityMenusUseCase;
    private final ListSecuritySubMenusUseCase listSecuritySubMenusUseCase;
    private final ListAdminSecurityMenuTreeUseCase listAdminSecurityMenuTreeUseCase;
    private final ListUserSecurityMenuTreeUseCase listUserSecurityMenuTreeUseCase;

    @GetMapping
    public List<SecurityMenuTreeResponse> listMenuTree(@RequestParam(required = false, defaultValue = "true") Boolean active) {
        log.info("LOG INICIO X = listSecurityMenuTreeController active={}", active);
        List<SecurityMenuTreeResponse> response = listSecurityMenuTreeUseCase.listTree(active);
        log.info("LOG FIN X = listSecurityMenuTreeController size={}", response.size());
        return response;
    }

    @GetMapping("/flat")
    public List<SecurityMenuResponse> listMenus(@RequestParam(required = false, defaultValue = "true") Boolean active) {
        log.info("LOG INICIO X = listSecurityMenusController active={}", active);
        List<SecurityMenuResponse> response = listSecurityMenusUseCase.list(active);
        log.info("LOG FIN X = listSecurityMenusController size={}", response.size());
        return response;
    }

    @GetMapping("/{menuId}/sub-menus")
    public List<SecuritySubMenuResponse> listSubMenus(@PathVariable Long menuId,
                                                       @RequestParam(required = false, defaultValue = "true") Boolean active) {
        log.info("LOG INICIO X = listSecuritySubMenusController menuId={} active={}", menuId, active);
        List<SecuritySubMenuResponse> response = listSecuritySubMenusUseCase.listByMenuId(menuId, active);
        log.info("LOG FIN X = listSecuritySubMenusController size={}", response.size());
        return response;
    }

    @GetMapping("/roles/admin")
    public List<SecurityMenuTreeResponse> listAdminMenuTree() {
        return listAdminSecurityMenuTreeUseCase.listAdminMenuTree();
    }

    @GetMapping("/users/{username}")
    public List<SecurityMenuTreeResponse> listUserMenuTree(@PathVariable String username) {
        return listUserSecurityMenuTreeUseCase.listUserMenuTree(username);
    }
}
