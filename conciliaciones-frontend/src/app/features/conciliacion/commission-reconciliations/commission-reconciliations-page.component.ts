import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { CommissionReconciliation } from '../../../models/commission-reconciliation.model';
import { CommissionReconciliationService } from '../../../services/commission-reconciliation.service';
import { CommissionReconciliationGeneratePaymentDialogComponent } from './commission-reconciliation-generate-payment-dialog.component';

@Component({
  selector: 'app-commission-reconciliations-page',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatPaginatorModule,
    MatSnackBarModule,
    MatSortModule,
    MatTableModule,
    EmptyStateComponent,
    LoadingSpinnerComponent,
    PageHeaderComponent
  ],
  template: `
    <div class="page-shell">
      <app-page-header
        title="Casos de conciliación"
        subtitle="Comisiones pendientes de pago por estado de póliza inválido o Net amount negativo."
      />

      <form [formGroup]="form" class="filters" (ngSubmit)="search()">
        <mat-form-field appearance="outline">
          <mat-label>Producer</mat-label>
          <input matInput formControlName="producerName" placeholder="Nombre del producer" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Policy</mat-label>
          <input matInput formControlName="policyNumber" placeholder="Número de póliza" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Agent</mat-label>
          <input matInput formControlName="agencyName" placeholder="Nombre de agencia" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Carrier</mat-label>
          <input matInput formControlName="carrierName" placeholder="Nombre de carrier" />
        </mat-form-field>

        <div class="filter-actions">
          <button mat-raised-button color="primary" type="submit">
            <mat-icon>search</mat-icon>
            Buscar
          </button>
          <button mat-button type="button" (click)="clear()">
            Limpiar
          </button>
        </div>
      </form>

      @if (loading) {
        <app-loading-spinner />
      } @else if (!rows.length) {
        <app-empty-state title="Sin casos de conciliación" description="No hay comisiones pendientes para los filtros aplicados." />
      } @else {
        <div class="table-wrapper">
          <table mat-table [dataSource]="rows" matSort>
            @for (column of columns; track column) {
              <ng-container [matColumnDef]="column">
                <th mat-header-cell *matHeaderCellDef mat-sort-header>{{ headers[column] ?? column }}</th>
                <td mat-cell *matCellDef="let row">
                  @if (column === 'actions') {
                    <button mat-stroked-button color="primary" type="button" (click)="openGeneratePaymentDialog(row)">
                      <mat-icon>payments</mat-icon>
                      Generar pago
                    </button>
                  } @else if (column === 'reconciliationReason') {
                    <span class="reason" [class.reason-negative]="row.reconciliationType === 'NET_AMOUNT_NEGATIVE'">
                      {{ displayValue(row, column) }}
                    </span>
                  } @else {
                    {{ displayValue(row, column) }}
                  }
                </td>
              </ng-container>
            }

            <tr mat-header-row *matHeaderRowDef="columns"></tr>
            <tr mat-row *matRowDef="let row; columns: columns"></tr>
          </table>

          <mat-paginator [pageSize]="10" [pageSizeOptions]="[5, 10, 25]" showFirstLastButtons />
        </div>
      }
    </div>
  `,
  styles: [
    `.page-shell{display:block}`,
    `.filters{display:grid;grid-template-columns:repeat(4,minmax(180px,1fr));gap:12px;align-items:start;margin-bottom:12px}`,
    `.filter-actions{display:flex;gap:8px;align-items:center;min-height:56px}`,
    `.table-wrapper{overflow:auto;background:var(--mat-sys-surface);border-radius:12px}`,
    `.reason{display:inline-flex;border-radius:999px;padding:4px 10px;background:rgba(255,152,0,.14);white-space:nowrap}`,
    `.reason-negative{background:rgba(244,67,54,.14)}`,
    `table{width:100%;min-width:1320px}`,
    `button mat-icon{margin-right:4px}`,
    `@media(max-width:1100px){.filters{grid-template-columns:repeat(2,minmax(180px,1fr))}}`,
    `@media(max-width:640px){.filters{grid-template-columns:1fr}.filter-actions{justify-content:flex-start}}`
  ]
})
export class CommissionReconciliationsPageComponent implements OnInit {
  loading = true;
  rows: CommissionReconciliation[] = [];

  readonly form = this.fb.group({
    producerName: [''],
    policyNumber: [''],
    agencyName: [''],
    carrierName: ['']
  });

  readonly columns = [
    'policyNumber',
    'commissionStatementId',
    'clientName',
    'producerName',
    'agencyName',
    'carrierName',
    'policyStatusName',
    'netAmount',
    'rate',
    'commissionRatePct',
    'estimatedPaymentAmount',
    'reconciliationReason',
    'createdAt',
    'actions'
  ];

  readonly headers: Record<string, string> = {
    policyNumber: 'Póliza',
    commissionStatementId: 'Statement',
    clientName: 'Cliente',
    producerName: 'Producer',
    agencyName: 'Agent',
    carrierName: 'Carrier',
    policyStatusName: 'Estado póliza',
    netAmount: 'Net amount',
    rate: 'Rate',
    commissionRatePct: 'Commission %',
    estimatedPaymentAmount: 'Pago estimado',
    reconciliationReason: 'Motivo',
    createdAt: 'Fecha creación',
    actions: 'Acciones'
  };

  constructor(
    private readonly fb: FormBuilder,
    private readonly service: CommissionReconciliationService,
    private readonly dialog: MatDialog,
    private readonly snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.load();
  }

  search(): void {
    this.load();
  }

  clear(): void {
    this.form.reset({
      producerName: '',
      policyNumber: '',
      agencyName: '',
      carrierName: ''
    });
    this.load();
  }

  openGeneratePaymentDialog(row: CommissionReconciliation): void {
    if (!row.commissionStatementItemId) {
      return;
    }

    this.service.getById(row.commissionStatementItemId).subscribe({
      next: (detail) => {
        const reconciliation = this.mergeReconciliation(row, detail);
        const dialogRef = this.dialog.open(CommissionReconciliationGeneratePaymentDialogComponent, {
          width: '820px',
          maxWidth: '96vw',
          data: { reconciliation }
        });

        dialogRef.afterClosed().subscribe((updated?: CommissionReconciliation) => {
          if (!updated) {
            return;
          }

          this.rows = this.rows.filter(
            (item) => item.commissionStatementItemId !== row.commissionStatementItemId
          );

          this.snackBar.open('Pago de comisión generado correctamente.', 'Cerrar', { duration: 2800 });
        });
      },
      error: () => {
        this.snackBar.open('No fue posible consultar el caso seleccionado.', 'Cerrar', { duration: 3200 });
      }
    });
  }

  displayValue(row: CommissionReconciliation, column: string): string {
    const value = (row as any)[column];

    if (value === null || value === undefined || value === '') {
      return '—';
    }

    if (['netAmount', 'rate', 'commissionRatePct', 'estimatedPaymentAmount'].includes(column)) {
      return this.formatNumber(value);
    }

    if (column === 'createdAt') {
      return this.formatDate(value);
    }

    return String(value);
  }

  private load(): void {
    this.loading = true;

    this.service.list(this.form.getRawValue()).subscribe({
      next: (data) => {
        this.rows = data;
        this.loading = false;
      },
      error: () => {
        this.rows = [];
        this.loading = false;
        this.snackBar.open('No fue posible consultar los casos de conciliación.', 'Cerrar', { duration: 3200 });
      }
    });
  }

  private mergeReconciliation(
    current: CommissionReconciliation,
    updated: CommissionReconciliation
  ): CommissionReconciliation {
    return {
      ...current,
      commissionStatementItemId: updated.commissionStatementItemId ?? current.commissionStatementItemId,
      commissionStatementId: updated.commissionStatementId ?? current.commissionStatementId,
      policyId: updated.policyId ?? current.policyId,
      policyNumber: updated.policyNumber ?? current.policyNumber,
      policyStatusId: updated.policyStatusId ?? current.policyStatusId,
      policyStatusName: updated.policyStatusName ?? current.policyStatusName,
      clientId: updated.clientId ?? current.clientId,
      clientName: updated.clientName ?? current.clientName,
      producerId: updated.producerId ?? current.producerId,
      producerName: updated.producerName ?? current.producerName,
      agencyId: updated.agencyId ?? current.agencyId,
      agencyName: updated.agencyName ?? current.agencyName,
      carrierId: updated.carrierId ?? current.carrierId,
      carrierName: updated.carrierName ?? current.carrierName,
      netAmount: updated.netAmount ?? current.netAmount,
      rate: updated.rate ?? current.rate,
      commissionRatePct: updated.commissionRatePct ?? current.commissionRatePct,
      estimatedPaymentAmount: updated.estimatedPaymentAmount ?? current.estimatedPaymentAmount,
      reconciliationType: updated.reconciliationType ?? current.reconciliationType,
      reconciliationReason: updated.reconciliationReason ?? current.reconciliationReason,
      createdAt: updated.createdAt ?? current.createdAt
    };
  }

  private formatNumber(value: unknown): string {
    const numberValue = Number(value ?? 0);
    if (Number.isNaN(numberValue)) {
      return '0.00';
    }
    return new Intl.NumberFormat('en-US', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(numberValue);
  }

  private formatDate(value: string): string {
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return value;
    }
    return new Intl.DateTimeFormat('es-CO', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    }).format(date);
  }
}
