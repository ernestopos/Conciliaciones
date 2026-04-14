import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { PageHeaderComponent } from '../page-header/page-header.component';
import { SearchFilterBarComponent } from '../search-filter-bar/search-filter-bar.component';
import { LoadingSpinnerComponent } from '../loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../empty-state/empty-state.component';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';
import { CrudFormDialogComponent } from '../crud-form-dialog/crud-form-dialog.component';
import { GenericTableComponent, TableAction } from '../generic-table/generic-table.component';
import { CrudRouteConfig } from '../../../core/models/crud.models';
import { CrudServiceRegistry } from '../../../core/services/crud-service.registry';

@Component({
  selector: 'app-crud-entity-page',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatDialogModule,
    MatIconModule,
    MatSnackBarModule,
    PageHeaderComponent,
    SearchFilterBarComponent,
    LoadingSpinnerComponent,
    EmptyStateComponent,
    GenericTableComponent
  ],
  template: `
    <div class="page-shell">
      <div class="page-header-row">
        <app-page-header [title]="config.title" [subtitle]="config.subtitle" />
        <button mat-raised-button color="primary" type="button" (click)="openCreate()">
          <mat-icon>add</mat-icon>
          {{ config.createLabel }}
        </button>
      </div>

      <app-search-filter-bar (filterChange)="applyFilter($event)" />

      @if (loading) {
        <app-loading-spinner />
      } @else if (!filteredRows.length) {
        <app-empty-state [title]="'Sin registros'" [description]="'No hay datos disponibles para ' + config.title.toLowerCase() + '.'" />
      } @else {
        <app-generic-table [data]="filteredRows" [displayedColumns]="config.columns" [headers]="config.headers" [actions]="tableActions" />
      }
    </div>
  `,
  styles: [
    `.page-shell{display:block}`,
    `.page-header-row{display:flex;justify-content:space-between;align-items:flex-end;gap:16px;flex-wrap:wrap;margin-bottom:8px}`
  ]
})
export class CrudEntityPageComponent implements OnInit {
  loading = true;
  rows: Record<string, any>[] = [];
  filteredRows: Record<string, any>[] = [];
  config!: CrudRouteConfig;
  tableActions: TableAction<Record<string, any>>[] = [];

  constructor(
    private readonly route: ActivatedRoute,
    private readonly serviceRegistry: CrudServiceRegistry,
    private readonly dialog: MatDialog,
    private readonly snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.config = this.route.snapshot.data['crudConfig'] as CrudRouteConfig;
    this.configureActions();
    this.load();
  }

  applyFilter(query: string): void {
    const normalized = query.trim().toLowerCase();
    if (!normalized) {
      this.filteredRows = [...this.rows];
      return;
    }

    this.filteredRows = this.rows.filter((item) => JSON.stringify(item).toLowerCase().includes(normalized));
  }

  openCreate(): void {
    const dialogRef = this.dialog.open(CrudFormDialogComponent, {
      width: '920px',
      maxWidth: '96vw',
      data: {
        title: this.config.createLabel,
        fields: this.config.fields,
        initialValue: this.createDefaultValue()
      }
    });

    dialogRef.afterClosed().subscribe((payload) => {
      if (!payload) {
        return;
      }

      this.serviceRegistry.get(this.config.resourceKey).create(payload).subscribe({
        next: () => {
          this.snackBar.open('Registro creado correctamente.', 'Cerrar', { duration: 2400 });
          this.load();
        },
        error: () => this.snackBar.open('No fue posible crear el registro.', 'Cerrar', { duration: 3200 })
      });
    });
  }

  private openEdit(row: Record<string, any>): void {
    const dialogRef = this.dialog.open(CrudFormDialogComponent, {
      width: '920px',
      maxWidth: '96vw',
      data: {
        title: `Editar ${this.config.title}`,
        fields: this.config.fields,
        initialValue: row
      }
    });

    dialogRef.afterClosed().subscribe((payload) => {
      if (!payload || row['id'] == null) {
        return;
      }

      this.serviceRegistry.get(this.config.resourceKey).update(row['id'], payload).subscribe({
        next: () => {
          this.snackBar.open('Registro actualizado correctamente.', 'Cerrar', { duration: 2400 });
          this.load();
        },
        error: () => this.snackBar.open('No fue posible actualizar el registro.', 'Cerrar', { duration: 3200 })
      });
    });
  }

  private confirmDelete(row: Record<string, any>): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '420px',
      data: {
        title: 'Eliminar registro',
        message: `¿Confirma la eliminación del registro ${this.getRowLabel(row)}?`
      }
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (!confirmed || row['id'] == null) {
        return;
      }

      this.serviceRegistry.get(this.config.resourceKey).delete(row['id']).subscribe({
        next: () => {
          this.snackBar.open('Registro eliminado correctamente.', 'Cerrar', { duration: 2400 });
          this.load();
        },
        error: () => this.snackBar.open('No fue posible eliminar el registro.', 'Cerrar', { duration: 3200 })
      });
    });
  }

  private reprocess(row: Record<string, any>): void {
    if (!this.config.allowReprocess || row['id'] == null) {
      return;
    }

    const service = this.serviceRegistry.get(this.config.resourceKey);
    if (!service.reprocess) {
      return;
    }

    service.reprocess(row['id']).subscribe({
      next: () => this.snackBar.open('Caso enviado a reproceso correctamente.', 'Cerrar', { duration: 2600 }),
      error: () => this.snackBar.open('No fue posible reprocesar el caso.', 'Cerrar', { duration: 3200 })
    });
  }

  private load(): void {
    this.loading = true;
    this.serviceRegistry.get(this.config.resourceKey).list().subscribe({
      next: (data) => {
        this.rows = data;
        this.filteredRows = [...data];
        this.loading = false;
      },
      error: () => {
        this.rows = [];
        this.filteredRows = [];
        this.loading = false;
        this.snackBar.open('No fue posible consultar la información.', 'Cerrar', { duration: 3200 });
      }
    });
  }

  private configureActions(): void {
    this.tableActions = [
      {
        label: 'Editar',
        icon: 'edit',
        callback: (row) => this.openEdit(row)
      },
      {
        label: 'Eliminar',
        icon: 'delete',
        color: 'warn',
        callback: (row) => this.confirmDelete(row)
      }
    ];

    if (this.config.allowReprocess) {
      this.tableActions.unshift({
        label: 'Reprocesar',
        icon: 'sync',
        color: 'primary',
        callback: (row) => this.reprocess(row)
      });
    }
  }

  private getRowLabel(row: Record<string, any>): string {
    return String(row['name'] ?? row['fullName'] ?? row['description'] ?? row['id']);
  }

  private createDefaultValue(): Record<string, any> {
    return this.config.fields.reduce<Record<string, any>>((acc, field) => {
      acc[field.key] = field.type === 'checkbox' ? true : '';
      return acc;
    }, {});
  }
}
