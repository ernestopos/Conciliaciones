import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AuditLog } from '../../../models/audit-log.model';

@Component({
  selector: 'app-audit-log-detail-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule, MatIconModule],
  template: `
    <h2 mat-dialog-title>
      <mat-icon>manage_search</mat-icon>
      Detalle de auditoría
    </h2>

    <mat-dialog-content class="content">
      <section class="summary-grid">
        <div><strong>ID</strong><span>{{ data.id }}</span></div>
        <div><strong>Entidad</strong><span>{{ data.entityName }}</span></div>
        <div><strong>ID Entidad</strong><span>{{ data.entityId }}</span></div>
        <div><strong>Acción</strong><span>{{ data.actionName || data.actionId }}</span></div>
        <div><strong>Usuario</strong><span>{{ data.username || '—' }}</span></div>
        <div><strong>Fecha evento</strong><span>{{ data.eventTimestamp | date:'yyyy-MM-dd HH:mm:ss' }}</span></div>
      </section>

      <section class="details" *ngIf="data.details">
        <h3>Detalle</h3>
        <p>{{ data.details }}</p>
      </section>

      <section class="json-grid">
        <article>
          <h3>Valores anteriores</h3>
          <pre>{{ pretty(data.oldValues) }}</pre>
        </article>
        <article>
          <h3>Valores nuevos</h3>
          <pre>{{ pretty(data.newValues) }}</pre>
        </article>
      </section>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cerrar</button>
    </mat-dialog-actions>
  `,
  styles: [`
    h2{display:flex;align-items:center;gap:8px}
    .content{display:flex;flex-direction:column;gap:18px;min-width:min(900px,90vw)}
    .summary-grid{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:12px}
    .summary-grid div{border:1px solid #e0e0e0;border-radius:12px;padding:12px;background:#fafafa;display:flex;flex-direction:column;gap:4px}
    .summary-grid strong{font-size:12px;color:#666;text-transform:uppercase;letter-spacing:.04em}
    .details{border:1px solid #e0e0e0;border-radius:12px;padding:12px;background:#fff}
    .details h3,.json-grid h3{margin:0 0 8px;font-size:15px}
    .json-grid{display:grid;grid-template-columns:1fr 1fr;gap:16px}
    pre{max-height:360px;overflow:auto;margin:0;padding:12px;border-radius:12px;background:#111;color:#f5f5f5;font-size:12px;white-space:pre-wrap;word-break:break-word}
    @media (max-width: 900px){.summary-grid,.json-grid{grid-template-columns:1fr}.content{min-width:0}}
  `]
})
export class AuditLogDetailDialogComponent {
  constructor(@Inject(MAT_DIALOG_DATA) readonly data: AuditLog) {}

  pretty(value: unknown): string {
    if (value === null || value === undefined || value === '') {
      return '—';
    }
    return JSON.stringify(value, null, 2);
  }
}
