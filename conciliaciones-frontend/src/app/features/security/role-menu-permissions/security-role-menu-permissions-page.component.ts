import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin, finalize } from 'rxjs';

import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';

import { SecurityMenuTree, SecuritySubMenuTree } from '../../../models/security-menu.model';
import { SecurityRole } from '../../../models/security-role.model';
import { SecurityMenuService } from '../../../services/security-menu.service';
import { SecurityRoleMenuPermissionService } from '../../../services/security-role-menu-permission.service';
import { SecurityRoleService } from '../../../services/security-role.service';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-security-role-menu-permissions-page',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatButtonModule,
    MatCardModule,
    MatCheckboxModule,
    MatDividerModule,
    MatFormFieldModule,
    MatIconModule,
    MatProgressBarModule,
    MatSelectModule,
    MatSnackBarModule,
    MatTooltipModule,
    EmptyStateComponent,
    PageHeaderComponent
  ],
  template: `
    <div class="page-shell">
      <app-page-header
        title="Configuración Funciones a Roles"
        subtitle="Seleccione un rol y marque las opciones de menú que tendrá disponibles en la aplicación."
      />

      <mat-card class="config-card">
        <mat-card-header>
          <mat-card-title>Rol del sistema</mat-card-title>
          <mat-card-subtitle>Los permisos configurados reemplazan la selección actual del rol.</mat-card-subtitle>

          <span class="spacer"></span>

          <button
            mat-stroked-button
            color="primary"
            type="button"
            [disabled]="loading || saving"
            (click)="refresh()"
          >
            <mat-icon>refresh</mat-icon>
            Actualizar
          </button>
        </mat-card-header>

        @if (loading) {
          <mat-progress-bar mode="indeterminate" />
        }

        <mat-card-content>
          <div class="role-selector-row">
            <mat-form-field appearance="outline" class="role-field">
              <mat-label>Rol</mat-label>
              <mat-select
                [(ngModel)]="selectedRoleId"
                [disabled]="loading || saving"
                (selectionChange)="onRoleChange($event.value)"
              >
                <mat-option [value]="null">Seleccione un rol</mat-option>
                @for (role of roles; track role.id) {
                  <mat-option [value]="role.id">
                    {{ role.name }} {{ role.code ? '(' + role.code + ')' : '' }}
                  </mat-option>
                }
              </mat-select>
            </mat-form-field>

            <div class="summary-card">
              <span class="summary-label">Funciones seleccionadas</span>
              <strong>{{ selectedSubMenuIds.size }}</strong>
            </div>
          </div>
        </mat-card-content>
      </mat-card>

      <mat-card class="config-card tree-card">
        <mat-card-header>
          <mat-card-title>Árbol de funciones</mat-card-title>
          <mat-card-subtitle>Marque los submenús que estarán habilitados para el rol seleccionado.</mat-card-subtitle>
        </mat-card-header>

        @if (permissionsLoading || saving) {
          <mat-progress-bar mode="indeterminate" />
        }

        <mat-card-content>
          @if (!loading && !menus.length) {
            <app-empty-state
              title="Sin menús configurados"
              description="No se encontraron menús activos para configurar."
            />
          } @else {
            <div class="toolbar-actions">
              <button
                mat-stroked-button
                color="primary"
                type="button"
                [disabled]="!selectedRoleId || permissionsLoading || saving"
                (click)="selectAll()"
              >
                <mat-icon>done_all</mat-icon>
                Seleccionar todo
              </button>

              <button
                mat-stroked-button
                type="button"
                [disabled]="!selectedRoleId || permissionsLoading || saving"
                (click)="clearSelection()"
              >
                <mat-icon>clear_all</mat-icon>
                Limpiar selección
              </button>
            </div>

            <div class="menus-tree" [class.disabled]="!selectedRoleId || permissionsLoading || saving">
              @for (menu of menus; track menu.id) {
                <section class="menu-block">
                  <div class="menu-header">
                    <mat-checkbox
                      [checked]="isMenuFullySelected(menu)"
                      [indeterminate]="isMenuPartiallySelected(menu)"
                      [disabled]="!selectedRoleId || permissionsLoading || saving"
                      (change)="toggleMenu(menu, $event.checked)"
                    >
                      <span class="menu-title">
                        <mat-icon>{{ menu.icon || 'folder' }}</mat-icon>
                        {{ getMenuLabel(menu) }}
                      </span>
                    </mat-checkbox>
                  </div>

                  <mat-divider />

                  <div class="sub-menu-list">
                    @if (!menu.children?.length) {
                      <span class="muted">Este menú no tiene submenús configurados.</span>
                    } @else {
                      @for (subMenu of menu.children; track subMenu.id) {
                        <mat-checkbox
                          class="sub-menu-check"
                          [checked]="isSubMenuSelected(subMenu.id)"
                          [disabled]="!selectedRoleId || permissionsLoading || saving"
                          (change)="toggleSubMenu(subMenu.id, $event.checked)"
                        >
                          <span class="sub-menu-title">
                            <mat-icon>{{ subMenu.icon || 'chevron_right' }}</mat-icon>
                            <span>{{ getSubMenuLabel(subMenu) }}</span>
                            <small>{{ subMenu.route }}</small>
                          </span>
                        </mat-checkbox>
                      }
                    }
                  </div>
                </section>
              }
            </div>

            <div class="footer-actions">
              <button
                mat-flat-button
                color="primary"
                type="button"
                [disabled]="!selectedRoleId || permissionsLoading || saving"
                (click)="save()"
              >
                <mat-icon>save</mat-icon>
                Guardar configuración
              </button>
            </div>
          }
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [
    `.page-shell{display:block}`,
    `.config-card{border-radius:14px;overflow:hidden}`,
    `.tree-card{margin-top:18px}`,
    `mat-card-header{display:flex;align-items:center;padding:20px 20px 12px}`,
    `mat-card-content{padding:16px 20px 20px}`,
    `.spacer{flex:1 1 auto}`,
    `.role-selector-row{display:flex;gap:16px;align-items:stretch;flex-wrap:wrap}`,
    `.role-field{min-width:320px;max-width:520px;flex:1}`,
    `.summary-card{border:1px solid #e2e8f0;border-radius:12px;padding:10px 16px;min-width:190px;display:flex;flex-direction:column;justify-content:center;background:#f8fafc}`,
    `.summary-label{font-size:12px;color:#64748b}`,
    `.summary-card strong{font-size:26px;line-height:1.1;color:#0f172a}`,
    `.toolbar-actions{display:flex;gap:10px;margin-bottom:16px;flex-wrap:wrap}`,
    `.menus-tree{display:grid;grid-template-columns:repeat(auto-fit,minmax(320px,1fr));gap:16px}`,
    `.menus-tree.disabled{opacity:.72}`,
    `.menu-block{border:1px solid #e2e8f0;border-radius:14px;background:#fff;overflow:hidden}`,
    `.menu-header{padding:14px 16px;background:#f8fafc}`,
    `.menu-title{display:inline-flex;align-items:center;gap:8px;font-weight:700;color:#0f172a}`,
    `.sub-menu-list{display:flex;flex-direction:column;gap:10px;padding:14px 16px}`,
    `.sub-menu-check{width:100%}`,
    `.sub-menu-title{display:inline-flex;align-items:center;gap:8px;flex-wrap:wrap}`,
    `.sub-menu-title small{color:#64748b;margin-left:4px}`,
    `.muted{color:#94a3b8;font-size:13px}`,
    `.footer-actions{display:flex;justify-content:flex-end;margin-top:20px}`,
    `button mat-icon{margin-right:6px}`,
    `@media(max-width:720px){mat-card-header{align-items:flex-start;gap:12px;flex-direction:column}.spacer{display:none}mat-card-header button,.role-field,.footer-actions button{width:100%}.menus-tree{grid-template-columns:1fr}}`
  ]
})
export class SecurityRoleMenuPermissionsPageComponent implements OnInit {
  roles: SecurityRole[] = [];
  menus: SecurityMenuTree[] = [];
  selectedRoleId: number | null = null;
  selectedSubMenuIds = new Set<number>();

  loading = false;
  permissionsLoading = false;
  saving = false;

  constructor(
    private readonly roleService: SecurityRoleService,
    private readonly menuService: SecurityMenuService,
    private readonly permissionService: SecurityRoleMenuPermissionService,
    private readonly snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.refresh();
  }

  refresh(): void {
    this.loading = true;

    forkJoin({
      roles: this.roleService.list(),
      menus: this.menuService.findTree()
    })
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: ({ roles, menus }) => {
          this.roles = (roles ?? []).filter(role => role.active !== false);
          this.menus = menus ?? [];

          if (this.selectedRoleId) {
            this.loadPermissions(this.selectedRoleId);
          }
        },
        error: () => {
          this.roles = [];
          this.menus = [];
          this.snackBar.open('No fue posible cargar roles y menús.', 'Cerrar', { duration: 5000 });
        }
      });
  }

  onRoleChange(roleId: number | null): void {
    this.selectedSubMenuIds.clear();

    if (!roleId) {
      return;
    }

    this.loadPermissions(roleId);
  }

  loadPermissions(roleId: number): void {
    this.permissionsLoading = true;
    this.selectedSubMenuIds.clear();

    this.permissionService.findByRoleId(roleId)
      .pipe(finalize(() => this.permissionsLoading = false))
      .subscribe({
        next: permissions => {
          (permissions ?? [])
            .filter(permission => permission.active !== false && !!permission.subMenuId)
            .forEach(permission => this.selectedSubMenuIds.add(Number(permission.subMenuId)));
        },
        error: () => {
          this.selectedSubMenuIds.clear();
          this.snackBar.open('No fue posible consultar los permisos del rol.', 'Cerrar', { duration: 5000 });
        }
      });
  }

  save(): void {
    if (!this.selectedRoleId) {
      this.snackBar.open('Debe seleccionar un rol.', 'Cerrar', { duration: 4000 });
      return;
    }

    const request = {
      subMenuIds: Array.from(this.selectedSubMenuIds)
    };

    this.saving = true;

    this.permissionService.save(this.selectedRoleId, request)
      .pipe(finalize(() => this.saving = false))
      .subscribe({
        next: permissions => {
          this.selectedSubMenuIds.clear();
          (permissions ?? [])
            .filter(permission => permission.active !== false && !!permission.subMenuId)
            .forEach(permission => this.selectedSubMenuIds.add(Number(permission.subMenuId)));

          this.snackBar.open('Configuración guardada correctamente.', 'Cerrar', { duration: 4000 });
        },
        error: () => this.snackBar.open('No fue posible guardar la configuración.', 'Cerrar', { duration: 5000 })
      });
  }

  selectAll(): void {
    this.menus.forEach(menu => (menu.children ?? []).forEach(subMenu => this.selectedSubMenuIds.add(Number(subMenu.id))));
  }

  clearSelection(): void {
    this.selectedSubMenuIds.clear();
  }

  toggleMenu(menu: SecurityMenuTree, checked: boolean): void {
    (menu.children ?? []).forEach(subMenu => this.toggleSubMenu(subMenu.id, checked));
  }

  toggleSubMenu(subMenuId: number, checked: boolean): void {
    const id = Number(subMenuId);

    if (checked) {
      this.selectedSubMenuIds.add(id);
    } else {
      this.selectedSubMenuIds.delete(id);
    }
  }

  isSubMenuSelected(subMenuId: number): boolean {
    return this.selectedSubMenuIds.has(Number(subMenuId));
  }

  isMenuFullySelected(menu: SecurityMenuTree): boolean {
    const children = menu.children ?? [];
    return children.length > 0 && children.every(subMenu => this.isSubMenuSelected(subMenu.id));
  }

  isMenuPartiallySelected(menu: SecurityMenuTree): boolean {
    const children = menu.children ?? [];
    return children.some(subMenu => this.isSubMenuSelected(subMenu.id)) && !this.isMenuFullySelected(menu);
  }

  getMenuLabel(menu: SecurityMenuTree): string {
    return menu.label || menu.name || menu.code || `Menú ${menu.id}`;
  }

  getSubMenuLabel(subMenu: SecuritySubMenuTree): string {
    return subMenu.label || subMenu.name || subMenu.code || `Función ${subMenu.id}`;
  }
}
