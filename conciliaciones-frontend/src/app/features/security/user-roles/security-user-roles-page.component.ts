import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin, finalize } from 'rxjs';

import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';

import { SecurityRole } from '../../../models/security-role.model';
import { ConfiguredSecurityUser } from '../../../models/security-user.model';
import { SecurityUserRole } from '../../../models/security-user-role.model';
import { SecurityRoleService } from '../../../services/security-role.service';
import { SecurityUserService } from '../../../services/security-user.service';
import { SecurityUserRoleService } from '../../../services/security-user-role.service';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-security-user-roles-page',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatIconModule,
    MatProgressBarModule,
    MatSelectModule,
    MatSnackBarModule,
    MatTableModule,
    MatTooltipModule,
    EmptyStateComponent,
    PageHeaderComponent
  ],
  template: `
    <div class="page-shell">
      <app-page-header
        title="Asociar Rol a Usuario"
        subtitle="Seleccione un usuario configurado y asígnele un rol del sistema."
      />

      <mat-card class="association-card">
        <mat-card-header>
          <mat-card-title>Nueva asociación</mat-card-title>
          <mat-card-subtitle>Un usuario solo puede tener un rol activo dentro de la aplicación.</mat-card-subtitle>

          <span class="spacer"></span>

          <button
            mat-stroked-button
            color="primary"
            type="button"
            [disabled]="loading || saving || deletingUserIds.size > 0"
            (click)="refresh()"
          >
            <mat-icon>refresh</mat-icon>
            Actualizar
          </button>
        </mat-card-header>

        @if (loading || saving) {
          <mat-progress-bar mode="indeterminate" />
        }

        <mat-card-content>
          <div class="form-grid">
            <mat-form-field appearance="outline">
              <mat-label>Usuario configurado</mat-label>
              <mat-select [(ngModel)]="selectedUserId" [disabled]="loading || saving">
                <mat-option [value]="null">Seleccione un usuario</mat-option>
                @for (user of users; track user.id) {
                  <mat-option [value]="user.id">
                    {{ user.fullName || user.username }} {{ user.email ? '(' + user.email + ')' : '' }}
                  </mat-option>
                }
              </mat-select>
            </mat-form-field>

            <mat-form-field appearance="outline">
              <mat-label>Rol</mat-label>
              <mat-select [(ngModel)]="selectedRoleId" [disabled]="loading || saving">
                <mat-option [value]="null">Seleccione un rol</mat-option>
                @for (role of roles; track role.id) {
                  <mat-option [value]="role.id">
                    {{ role.name }} {{ role.code ? '(' + role.code + ')' : '' }}
                  </mat-option>
                }
              </mat-select>
            </mat-form-field>

            <button
              mat-flat-button
              color="primary"
              type="button"
              [disabled]="!selectedUserId || !selectedRoleId || loading || saving"
              (click)="save()"
            >
              <mat-icon>link</mat-icon>
              Asociar
            </button>
          </div>
        </mat-card-content>
      </mat-card>

      <mat-card class="association-card table-card">
        <mat-card-header>
          <mat-card-title>Usuarios con rol asignado</mat-card-title>
          <mat-card-subtitle>Listado de asociaciones activas entre usuarios configurados y roles.</mat-card-subtitle>
        </mat-card-header>

        @if (loading || deletingUserIds.size > 0) {
          <mat-progress-bar mode="indeterminate" />
        }

        <mat-card-content>
          @if (!loading && !associations.length) {
            <app-empty-state
              title="Sin asociaciones"
              description="Todavía no hay usuarios asociados a roles."
            />
          } @else {
            <div class="table-wrapper">
              <table mat-table [dataSource]="associations" class="associations-table">
                <ng-container matColumnDef="username">
                  <th mat-header-cell *matHeaderCellDef>Usuario</th>
                  <td mat-cell *matCellDef="let row">
                    <strong>{{ row.username || '-' }}</strong>
                    <small>{{ row.fullName || '-' }}</small>
                  </td>
                </ng-container>

                <ng-container matColumnDef="email">
                  <th mat-header-cell *matHeaderCellDef>Email</th>
                  <td mat-cell *matCellDef="let row">{{ row.email || '-' }}</td>
                </ng-container>

                <ng-container matColumnDef="role">
                  <th mat-header-cell *matHeaderCellDef>Rol</th>
                  <td mat-cell *matCellDef="let row">
                    <span class="role-badge">{{ row.roleName || row.roleCode || '-' }}</span>
                  </td>
                </ng-container>

                <ng-container matColumnDef="active">
                  <th mat-header-cell *matHeaderCellDef>Estado</th>
                  <td mat-cell *matCellDef="let row">
                    <span class="status" [class.enabled]="row.active" [class.disabled]="!row.active">
                      {{ row.active ? 'Activo' : 'Inactivo' }}
                    </span>
                  </td>
                </ng-container>

                <ng-container matColumnDef="actions">
                  <th mat-header-cell *matHeaderCellDef>Acciones</th>
                  <td mat-cell *matCellDef="let row">
                    <button
                      mat-icon-button
                      color="warn"
                      type="button"
                      matTooltip="Eliminar asociación"
                      [disabled]="deletingUserIds.has(row.userId)"
                      (click)="deleteAssociation(row)"
                    >
                      <mat-icon>link_off</mat-icon>
                    </button>
                  </td>
                </ng-container>

                <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
                <tr mat-row *matRowDef="let row; columns: displayedColumns"></tr>
              </table>
            </div>
          }
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [
    `.page-shell{display:block}`,
    `.association-card{border-radius:14px;overflow:hidden}`,
    `.table-card{margin-top:18px}`,
    `mat-card-header{display:flex;align-items:center;padding:20px 20px 12px}`,
    `mat-card-content{padding:16px 20px 20px}`,
    `.spacer{flex:1 1 auto}`,
    `.form-grid{display:grid;grid-template-columns:minmax(260px,1fr) minmax(220px,360px) auto;gap:14px;align-items:center}`,
    `.form-grid button{height:56px;margin-bottom:20px}`,
    `.table-wrapper{width:100%;overflow:auto}`,
    `.associations-table{width:100%;min-width:820px}`,
    `td strong{display:block;font-weight:700;color:#0f172a}`,
    `td small{display:block;color:#64748b;margin-top:2px}`,
    `.role-badge{display:inline-flex;border-radius:999px;padding:6px 12px;background:#e2e8f0;color:#0f172a;font-weight:600}`,
    `.status{display:inline-flex;align-items:center;border-radius:999px;padding:4px 10px;font-size:12px;font-weight:600}`,
    `.status.enabled{background:#dcfce7;color:#166534}`,
    `.status.disabled{background:#fee2e2;color:#991b1b}`,
    `button mat-icon{margin-right:6px}`,
    `button[mat-icon-button] mat-icon{margin-right:0}`,
    `@media(max-width:900px){.form-grid{grid-template-columns:1fr}.form-grid button{width:100%;margin-bottom:0}mat-card-header{align-items:flex-start;gap:12px;flex-direction:column}.spacer{display:none}mat-card-header button{width:100%}}`
  ]
})
export class SecurityUserRolesPageComponent implements OnInit {
  users: ConfiguredSecurityUser[] = [];
  roles: SecurityRole[] = [];
  associations: SecurityUserRole[] = [];

  selectedUserId: number | null = null;
  selectedRoleId: number | null = null;

  displayedColumns = ['username', 'email', 'role', 'active', 'actions'];

  loading = false;
  saving = false;
  deletingUserIds = new Set<number>();

  constructor(
    private readonly userService: SecurityUserService,
    private readonly roleService: SecurityRoleService,
    private readonly userRoleService: SecurityUserRoleService,
    private readonly snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.refresh();
  }

  refresh(): void {
    this.loading = true;

    forkJoin({
      users: this.userService.findConfiguredUsers(),
      roles: this.roleService.list(),
      associations: this.userRoleService.list(true)
    })
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: ({ users, roles, associations }) => {
          this.users = (users ?? []).filter(user => user.active !== false);
          this.roles = (roles ?? []).filter(role => role.active !== false);
          this.associations = associations ?? [];
        },
        error: () => {
          this.users = [];
          this.roles = [];
          this.associations = [];
          this.snackBar.open('No fue posible cargar usuarios, roles y asociaciones.', 'Cerrar', { duration: 5000 });
        }
      });
  }

  save(): void {
    if (!this.selectedUserId || !this.selectedRoleId) {
      this.snackBar.open('Debe seleccionar un usuario y un rol.', 'Cerrar', { duration: 4000 });
      return;
    }

    this.saving = true;

    this.userRoleService.save({
      userId: this.selectedUserId,
      roleId: this.selectedRoleId
    })
      .pipe(finalize(() => this.saving = false))
      .subscribe({
        next: () => {
          this.snackBar.open('Rol asociado correctamente al usuario.', 'Cerrar', { duration: 4000 });
          this.selectedUserId = null;
          this.selectedRoleId = null;
          this.loadAssociations();
        },
        error: () => {
          this.snackBar.open('No fue posible asociar el rol al usuario.', 'Cerrar', { duration: 5000 });
        }
      });
  }

  deleteAssociation(association: SecurityUserRole): void {
    const userLabel = association.fullName || association.username || 'el usuario';
    const confirmed = window.confirm(`¿Desea eliminar la asociación de rol para ${userLabel}?`);

    if (!confirmed) {
      return;
    }

    this.deletingUserIds.add(association.userId);

    this.userRoleService.deleteByUserId(association.userId)
      .pipe(finalize(() => this.deletingUserIds.delete(association.userId)))
      .subscribe({
        next: () => {
          this.snackBar.open('Asociación eliminada correctamente.', 'Cerrar', { duration: 4000 });
          this.loadAssociations();
        },
        error: () => {
          this.snackBar.open('No fue posible eliminar la asociación.', 'Cerrar', { duration: 5000 });
        }
      });
  }

  private loadAssociations(): void {
    this.userRoleService.list(true)
      .subscribe({
        next: associations => this.associations = associations ?? [],
        error: () => {
          this.associations = [];
          this.snackBar.open('No fue posible consultar las asociaciones.', 'Cerrar', { duration: 5000 });
        }
      });
  }
}
