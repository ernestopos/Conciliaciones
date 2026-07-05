import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { finalize } from 'rxjs';

import { ConfiguredSecurityUser, SecurityUser } from '../../../models/security-user.model';
import { SecurityUserService } from '../../../services/security-user.service';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-security-users-page',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatCardModule,
    MatChipsModule,
    MatIconModule,
    MatProgressBarModule,
    MatSnackBarModule,
    MatTableModule,
    MatTooltipModule,
    EmptyStateComponent,
    PageHeaderComponent
  ],
  template: `
    <div class="page-shell">
      <app-page-header
        title="Configuración de usuarios"
        subtitle="Consulta de usuarios creados en Keycloak y configuración de acceso a la aplicación."
      />

      <mat-card class="users-card">
        <mat-card-header>
          <mat-card-title>Usuarios Keycloak</mat-card-title>
          <mat-card-subtitle>Información sincronizada desde el servicio de seguridad</mat-card-subtitle>

          <span class="spacer"></span>

          <button
            mat-stroked-button
            color="primary"
            type="button"
            [disabled]="loading"
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
          @if (!loading && !users.length) {
            <app-empty-state
              title="Sin usuarios"
              description="No se encontraron usuarios creados en Keycloak."
            />
          } @else {
            <div class="table-wrapper">
              <table mat-table [dataSource]="users" class="users-table">
                <ng-container matColumnDef="username">
                  <th mat-header-cell *matHeaderCellDef>Usuario</th>
                  <td mat-cell *matCellDef="let row">{{ row.username || '-' }}</td>
                </ng-container>

                <ng-container matColumnDef="email">
                  <th mat-header-cell *matHeaderCellDef>Email</th>
                  <td mat-cell *matCellDef="let row">{{ row.email || '-' }}</td>
                </ng-container>

                <ng-container matColumnDef="fullName">
                  <th mat-header-cell *matHeaderCellDef>Nombre</th>
                  <td mat-cell *matCellDef="let row">{{ getFullName(row) }}</td>
                </ng-container>

                <ng-container matColumnDef="role">
                  <th mat-header-cell *matHeaderCellDef>Rol</th>
                  <td mat-cell *matCellDef="let row">
                    @if (row.role) {
                      <mat-chip>{{ row.role }}</mat-chip>
                    } @else {
                      <span class="muted">Sin rol</span>
                    }
                  </td>
                </ng-container>

                <ng-container matColumnDef="enabled">
                  <th mat-header-cell *matHeaderCellDef>Estado</th>
                  <td mat-cell *matCellDef="let row">
                    <span class="status" [class.enabled]="row.enabled" [class.disabled]="!row.enabled">
                      {{ row.enabled ? 'Activo' : 'Inactivo' }}
                    </span>
                  </td>
                </ng-container>

                <ng-container matColumnDef="configure">
                  <th mat-header-cell *matHeaderCellDef>Configurar</th>
                  <td mat-cell *matCellDef="let row">
                    <button
                      mat-icon-button
                      color="primary"
                      type="button"
                      [disabled]="isConfigured(row) || isConfiguring(row) || !row.enabled"
                      [matTooltip]="getConfigureTooltip(row)"
                      (click)="configureUser(row)"
                    >
                      <mat-icon>{{ isConfigured(row) ? 'check_circle' : 'settings' }}</mat-icon>
                    </button>
                  </td>
                </ng-container>

                <tr mat-header-row *matHeaderRowDef="keycloakDisplayedColumns"></tr>
                <tr mat-row *matRowDef="let row; columns: keycloakDisplayedColumns"></tr>
              </table>
            </div>
          }
        </mat-card-content>
      </mat-card>

      <mat-card class="users-card configured-card">
        <mat-card-header>
          <mat-card-title>Usuarios Configurados</mat-card-title>
          <mat-card-subtitle>Usuarios que ya tienen permisos sobre la aplicación</mat-card-subtitle>
        </mat-card-header>

        @if (configuredLoading) {
          <mat-progress-bar mode="indeterminate" />
        }

        <mat-card-content>
          @if (!configuredLoading && !configuredUsers.length) {
            <app-empty-state
              title="Sin usuarios configurados"
              description="Todavía no hay usuarios registrados en la aplicación."
            />
          } @else {
            <div class="table-wrapper">
              <table mat-table [dataSource]="configuredUsers" class="users-table configured-table">
                <ng-container matColumnDef="username">
                  <th mat-header-cell *matHeaderCellDef>Usuario</th>
                  <td mat-cell *matCellDef="let row">{{ row.username || '-' }}</td>
                </ng-container>

                <ng-container matColumnDef="email">
                  <th mat-header-cell *matHeaderCellDef>Email</th>
                  <td mat-cell *matCellDef="let row">{{ row.email || '-' }}</td>
                </ng-container>

                <ng-container matColumnDef="fullName">
                  <th mat-header-cell *matHeaderCellDef>Nombre</th>
                  <td mat-cell *matCellDef="let row">{{ row.fullName || '-' }}</td>
                </ng-container>

                <ng-container matColumnDef="active">
                  <th mat-header-cell *matHeaderCellDef>Estado</th>
                  <td mat-cell *matCellDef="let row">
                    <span class="status" [class.enabled]="row.active" [class.disabled]="!row.active">
                      {{ row.active ? 'Activo' : 'Inactivo' }}
                    </span>
                  </td>
                </ng-container>

                <ng-container matColumnDef="updatedAt">
                  <th mat-header-cell *matHeaderCellDef>Última actualización</th>
                  <td mat-cell *matCellDef="let row">{{ row.updatedAt ? (row.updatedAt | date:'dd/MM/yyyy HH:mm') : '-' }}</td>
                </ng-container>

                <tr mat-header-row *matHeaderRowDef="configuredDisplayedColumns"></tr>
                <tr mat-row *matRowDef="let row; columns: configuredDisplayedColumns"></tr>
              </table>
            </div>
          }
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [
    `.page-shell{display:block}`,
    `.users-card{border-radius:14px;overflow:hidden}`,
    `.configured-card{margin-top:18px}`,
    `mat-card-header{display:flex;align-items:center;padding:20px 20px 12px}`,
    `.spacer{flex:1 1 auto}`,
    `mat-card-content{padding:16px 20px 20px}`,
    `.table-wrapper{width:100%;overflow:auto}`,
    `.users-table{width:100%;min-width:860px}`,
    `.configured-table{min-width:780px}`,
    `.muted{color:#94a3b8;font-size:13px}`,
    `.status{display:inline-flex;align-items:center;border-radius:999px;padding:4px 10px;font-size:12px;font-weight:600}`,
    `.status.enabled{background:#dcfce7;color:#166534}`,
    `.status.disabled{background:#fee2e2;color:#991b1b}`,
    `button mat-icon{margin-right:0}`,
    `@media(max-width:720px){mat-card-header{align-items:flex-start;gap:12px;flex-direction:column}.spacer{display:none}mat-card-header button{width:100%}}`
  ]
})
export class SecurityUsersPageComponent implements OnInit {
  users: SecurityUser[] = [];
  configuredUsers: ConfiguredSecurityUser[] = [];

  loading = false;
  configuredLoading = false;
  configuringUsernames = new Set<string>();

  keycloakDisplayedColumns = ['username', 'email', 'fullName', 'role', 'enabled', 'configure'];
  configuredDisplayedColumns = ['username', 'email', 'fullName', 'active', 'updatedAt'];

  constructor(
    private readonly securityUserService: SecurityUserService,
    private readonly snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.refresh();
  }

  refresh(): void {
    this.loadUsers();
    this.loadConfiguredUsers();
  }

  loadUsers(): void {
    this.loading = true;

    this.securityUserService.findKeycloakUsers()
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: users => this.users = users ?? [],
        error: () => {
          this.users = [];
          this.snackBar.open('No fue posible consultar los usuarios de Keycloak.', 'Cerrar', { duration: 5000 });
        }
      });
  }

  loadConfiguredUsers(): void {
    this.configuredLoading = true;

    this.securityUserService.findConfiguredUsers()
      .pipe(finalize(() => this.configuredLoading = false))
      .subscribe({
        next: users => this.configuredUsers = users ?? [],
        error: () => {
          this.configuredUsers = [];
          this.snackBar.open('No fue posible consultar los usuarios configurados.', 'Cerrar', { duration: 5000 });
        }
      });
  }

  configureUser(user: SecurityUser): void {
    if (this.isConfigured(user) || this.isConfiguring(user)) {
      return;
    }

    const username = user.username?.trim();
    const email = user.email?.trim();
    const fullName = this.getFullName(user);

    if (!username || !email || fullName === '-') {
      this.snackBar.open('El usuario seleccionado no tiene la información completa para configurarlo.', 'Cerrar', { duration: 5000 });
      return;
    }

    this.configuringUsernames.add(username.toLowerCase());

    this.securityUserService.configureUser({
      username,
      email,
      fullName,
      active: user.enabled
    })
      .pipe(finalize(() => this.configuringUsernames.delete(username.toLowerCase())))
      .subscribe({
        next: () => {
          this.snackBar.open('Usuario configurado correctamente.', 'Cerrar', { duration: 4000 });
          this.loadConfiguredUsers();
        },
        error: () => {
          this.snackBar.open('No fue posible configurar el usuario seleccionado.', 'Cerrar', { duration: 5000 });
        }
      });
  }

  getFullName(user: SecurityUser): string {
    const fullName = [user.firstName, user.lastName]
      .filter(value => !!value && value.trim().length > 0)
      .join(' ');

    return fullName || '-';
  }

  isConfigured(user: SecurityUser): boolean {
    const username = user.username?.trim().toLowerCase();
    const email = user.email?.trim().toLowerCase();

    return this.configuredUsers.some(configuredUser => {
      const configuredUsername = configuredUser.username?.trim().toLowerCase();
      const configuredEmail = configuredUser.email?.trim().toLowerCase();

      return (!!username && configuredUsername === username) || (!!email && configuredEmail === email);
    });
  }

  isConfiguring(user: SecurityUser): boolean {
    const username = user.username?.trim().toLowerCase();
    return !!username && this.configuringUsernames.has(username);
  }

  getConfigureTooltip(user: SecurityUser): string {
    if (!user.enabled) {
      return 'Usuario inactivo en Keycloak';
    }

    if (this.isConfigured(user)) {
      return 'Usuario ya configurado';
    }

    if (this.isConfiguring(user)) {
      return 'Configurando usuario...';
    }

    return 'Configurar usuario';
  }
}
