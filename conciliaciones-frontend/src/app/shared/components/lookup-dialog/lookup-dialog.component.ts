import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { CrudLookupConfig } from '../../../core/models/crud.models';
import { CrudServiceRegistry } from '../../../core/services/crud-service.registry';

interface LookupDialogData {
  lookup: CrudLookupConfig;
}

@Component({
  selector: 'app-lookup-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule, MatIconModule, MatTableModule, MatInputModule, MatFormFieldModule],
  template: `
    <h2 mat-dialog-title>{{ data.lookup.title }}</h2>

    <mat-dialog-content>
      <mat-form-field appearance="outline" class="lookup-search">
        <mat-label>Buscar</mat-label>
        <input matInput type="text" (input)="applyFilter($event)" />
        <mat-icon matSuffix>search</mat-icon>
      </mat-form-field>

      <div class="lookup-table-wrapper">
        <table mat-table [dataSource]="filteredRows" class="lookup-table">
          <ng-container *ngFor="let column of data.lookup.columns" [matColumnDef]="column">
            <th mat-header-cell *matHeaderCellDef>{{ data.lookup.headers[column] ?? column }}</th>
            <td mat-cell *matCellDef="let row">{{ displayValue(row, column) }}</td>
          </ng-container>

          <ng-container matColumnDef="select">
            <th mat-header-cell *matHeaderCellDef>Acción</th>
            <td mat-cell *matCellDef="let row">
              <button mat-button color="primary" type="button" (click)="select(row)">Seleccionar</button>
            </td>
          </ng-container>

          <tr mat-header-row *matHeaderRowDef="renderedColumns"></tr>
          <tr mat-row *matRowDef="let row; columns: renderedColumns" (dblclick)="select(row)"></tr>
        </table>

        <div class="empty" *ngIf="!filteredRows.length">No hay registros disponibles.</div>
      </div>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button type="button" (click)="dialogRef.close()">Cancelar</button>
    </mat-dialog-actions>
  `,
  styles: [
    `.lookup-search{width:100%;margin-top:8px}`,
    `.lookup-table-wrapper{max-height:420px;overflow:auto}`,
    `.lookup-table{width:100%}`,
    `tr.mat-mdc-row{cursor:pointer}`,
    `.empty{padding:24px;text-align:center;color:#666}`
  ]
})
export class LookupDialogComponent implements OnInit {
  rows: Record<string, any>[] = [];
  filteredRows: Record<string, any>[] = [];

  get renderedColumns(): string[] {
    return [...this.data.lookup.columns, 'select'];
  }

  constructor(
    public readonly dialogRef: MatDialogRef<LookupDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public readonly data: LookupDialogData,
    private readonly serviceRegistry: CrudServiceRegistry
  ) {}

  ngOnInit(): void {
    this.serviceRegistry.get(this.data.lookup.resourceKey).list().subscribe({
      next: (rows) => {
        this.rows = rows;
        this.filteredRows = rows;
      },
      error: () => {
        this.rows = [];
        this.filteredRows = [];
      }
    });
  }

  applyFilter(event: Event): void {
    const value = (event.target as HTMLInputElement).value.trim().toLowerCase();
    this.filteredRows = value
      ? this.rows.filter((row) => JSON.stringify(row).toLowerCase().includes(value))
      : [...this.rows];
  }

  select(row: Record<string, any>): void {
    this.dialogRef.close(row);
  }

  displayValue(row: Record<string, any>, column: string): string {
    const value = row[column];
    if (value === null || value === undefined || value === '') {
      return '—';
    }
    if (typeof value === 'boolean') {
      return value ? 'Sí' : 'No';
    }
    return String(value);
  }
}
