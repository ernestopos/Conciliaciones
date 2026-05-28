import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { ValidationExecutionDetailModel } from '../../../models/processing-execution.model';

@Component({
  selector: 'app-validation-execution-detail-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule, MatIconModule],
  template: `
    <h2 mat-dialog-title>
      <mat-icon>fact_check</mat-icon>
      Detalle de validación
    </h2>

    <mat-dialog-content>
      @if (!data.rows.length) {
        <div class="empty">
          No hay registros de validación para esta ejecución.
        </div>
      } @else {
        <div class="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>Inicio</th>
                <th>Fin</th>
                <th>Exitoso</th>
                <th>Mensaje</th>
                <th>Validación</th>
                <th>Resultado</th>
              </tr>
            </thead>
            <tbody>
              @for (row of data.rows; track $index) {
                <tr>
                  <td>{{ display(row.startedAt) }}</td>
                  <td>{{ display(row.finishedAt) }}</td>
                  <td>{{ display(row.successful) }}</td>
                  <td>{{ display(row.message) }}</td>
                  <td>{{ display(row.validationTypeDescription) }}</td>
                  <td>{{ display(row.validationStatusDescription) }}</td>
                </tr>
              }
            </tbody>
          </table>
        </div>
      }
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-raised-button color="primary" mat-dialog-close>Cerrar</button>
    </mat-dialog-actions>
  `,
  styles: [`
    h2 {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    mat-dialog-content {
      min-width: min(1100px, 88vw);
      max-height: 70vh;
    }

    .table-wrapper {
      overflow: auto;
      border: 1px solid #e5e7eb;
      border-radius: 12px;
    }

    table {
      width: 100%;
      border-collapse: collapse;
      font-size: 13px;
    }

    th, td {
      padding: 10px 12px;
      border-bottom: 1px solid #e5e7eb;
      text-align: left;
      white-space: nowrap;
    }

    th {
      background: #f8fafc;
      font-weight: 700;
      color: #334155;
    }

    td {
      color: #475569;
    }

    tbody tr:last-child td {
      border-bottom: 0;
    }

    .empty {
      padding: 24px;
      color: #64748b;
      text-align: center;
    }
  `]
})
export class ValidationExecutionDetailDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public readonly data: { rows: ValidationExecutionDetailModel[] }
  ) {}

  display(value: unknown): string {
    if (value === null || value === undefined || value === '') return '—';
    if (typeof value === 'boolean') return value ? 'Sí' : 'No';
    return String(value);
  }
}
