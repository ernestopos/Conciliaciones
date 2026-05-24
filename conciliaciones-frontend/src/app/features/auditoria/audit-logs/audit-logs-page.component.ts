import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { GenericTableComponent, TableAction } from '../../../shared/components/generic-table/generic-table.component';
import { AuditLog } from '../../../models/audit-log.model';
import { AuditLogFilter, AuditLogService } from '../../../services/audit-log.service';
import { AuditLogDetailDialogComponent } from './audit-log-detail-dialog.component';

@Component({
  selector: 'app-audit-logs-page',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatDialogModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatSnackBarModule,
    PageHeaderComponent,
    LoadingSpinnerComponent,
    EmptyStateComponent,
    GenericTableComponent
  ],
  template: `
    <div class="page-shell">
      <div class="page-header-row">
        <app-page-header title="Auditoría" subtitle="Consulta de eventos y cambios realizados sobre las entidades del sistema." />
      </div>

      <mat-card class="filter-card">
        <form [formGroup]="filterForm" (ngSubmit)="load()" class="filter-grid">
          <mat-form-field appearance="outline">
            <mat-label>Entidad</mat-label>
            <input matInput formControlName="entityName" placeholder="PolicyEntity, CarrierEntity..." />
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>ID entidad</mat-label>
            <input matInput formControlName="entityId" />
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Acción ID</mat-label>
            <input matInput type="number" formControlName="actionId" />
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Usuario</mat-label>
            <input matInput formControlName="username" />
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Desde</mat-label>
            <input matInput type="datetime-local" formControlName="from" />
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Hasta</mat-label>
            <input matInput type="datetime-local" formControlName="to" />
          </mat-form-field>

          <div class="filter-actions">
            <button mat-stroked-button type="button" (click)="clearFilters()">
              <mat-icon>cleaning_services</mat-icon>
              Limpiar
            </button>
            <button mat-raised-button color="primary" type="submit">
              <mat-icon>search</mat-icon>
              Consultar
            </button>
          </div>
        </form>
      </mat-card>

      @if (loading) {
        <app-loading-spinner />
      } @else if (!rows.length) {
        <app-empty-state title="Sin registros" description="No hay eventos de auditoría para los filtros seleccionados." />
      } @else {
        <app-generic-table [data]="rows" [displayedColumns]="columns" [headers]="headers" [actions]="tableActions" />
      }
    </div>
  `,
  styles: [`
    .page-shell{display:block}
    .page-header-row{display:flex;justify-content:space-between;align-items:flex-end;gap:16px;flex-wrap:wrap;margin-bottom:8px}
    .filter-card{margin:12px 0 18px;border-radius:18px}
    .filter-grid{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:14px;align-items:start;padding:4px}
    .filter-actions{display:flex;justify-content:flex-end;align-items:center;gap:10px;grid-column:1/-1}
    button mat-icon{margin-right:6px}
    @media (max-width: 1100px){.filter-grid{grid-template-columns:repeat(2,minmax(0,1fr))}}
    @media (max-width: 700px){.filter-grid{grid-template-columns:1fr}.filter-actions{justify-content:stretch;flex-direction:column}.filter-actions button{width:100%}}
  `]
})
export class AuditLogsPageComponent implements OnInit {
  loading = true;
  rows: AuditLog[] = [];

  readonly columns = ['id', 'eventTimestamp', 'entityName', 'entityId', 'actionName', 'username', 'details'];
  readonly headers: Record<string, string> = {
    id: 'ID',
    eventTimestamp: 'Fecha evento',
    entityName: 'Entidad',
    entityId: 'ID entidad',
    actionName: 'Acción',
    username: 'Usuario',
    details: 'Detalle'
  };

  readonly tableActions: TableAction<AuditLog>[] = [
    {
      label: 'Ver detalle',
      icon: 'visibility',
      callback: (row) => this.openDetail(row)
    }
  ];

  readonly filterForm = this.fb.group({
    entityName: [''],
    entityId: [''],
    actionId: [''],
    username: [''],
    from: [''],
    to: ['']
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly auditLogService: AuditLogService,
    private readonly dialog: MatDialog,
    private readonly snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.auditLogService.list(this.buildFilter()).subscribe({
      next: (data) => {
        this.rows = data.map((item) => ({
          ...item,
          eventTimestamp: this.formatDateTime(item.eventTimestamp),
          actionName: item.actionName ?? String(item.actionId),
          details: item.details ?? '—'
        }));
        this.loading = false;
      },
      error: () => {
        this.rows = [];
        this.loading = false;
        this.snackBar.open('No fue posible consultar la auditoría.', 'Cerrar', { duration: 3200 });
      }
    });
  }

  clearFilters(): void {
    this.filterForm.reset({ entityName: '', entityId: '', actionId: '', username: '', from: '', to: '' });
    this.load();
  }

  private openDetail(row: AuditLog): void {
    if (!row.id) {
      return;
    }

    this.auditLogService.getById(row.id).subscribe({
      next: (detail) => this.dialog.open(AuditLogDetailDialogComponent, {
        width: '980px',
        maxWidth: '96vw',
        data: detail
      }),
      error: () => this.snackBar.open('No fue posible consultar el detalle de auditoría.', 'Cerrar', { duration: 3200 })
    });
  }

  private buildFilter(): AuditLogFilter {
    const raw = this.filterForm.getRawValue();
    return {
      entityName: raw.entityName ?? '',
      entityId: raw.entityId ?? '',
      actionId: raw.actionId ?? '',
      username: raw.username ?? '',
      from: this.toApiDateTime(raw.from),
      to: this.toApiDateTime(raw.to)
    };
  }

  private toApiDateTime(value: string | null | undefined): string {
    if (!value) {
      return '';
    }
    return value.length === 16 ? `${value}:00` : value;
  }

  private formatDateTime(value: string): string {
    if (!value) {
      return '—';
    }
    return value.replace('T', ' ').substring(0, 19);
  }
}
