import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { CommissionPayment } from '../../../models/commission-payment.model';
import { CommissionPaymentService } from '../../../services/commission-payment.service';
import { CommissionPaymentRecalculateDialogComponent } from './commission-payment-recalculate-dialog.component';

@Component({
  selector: 'app-commission-payments-page',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatPaginatorModule,
    MatSortModule,
    MatTableModule,
    MatDialogModule,
    MatSnackBarModule,
    EmptyStateComponent,
    LoadingSpinnerComponent,
    PageHeaderComponent
  ],
  template: `
    <div class="page-shell">
      <app-page-header
        title="Pagos / Liquidaciones"
        subtitle="Consulta de pagos calculados para productores por póliza."
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
        <app-empty-state title="Sin pagos" description="No hay pagos de comisiones para los filtros aplicados." />
      } @else {
        <div class="table-wrapper">
          <table mat-table [dataSource]="rows" matSort>
            @for (column of columns; track column) {
              <ng-container [matColumnDef]="column">
                <th mat-header-cell *matHeaderCellDef mat-sort-header>{{ headers[column] ?? column }}</th>
                <td mat-cell *matCellDef="let row">
                  @if (column === 'paymentAmount') {
                    <div class="payment-action">
                      <span>{{ displayValue(row, column) }}</span>
                      <button mat-icon-button color="primary" type="button" aria-label="Editar pago producer" (click)="openRecalculateDialog(row)">
                        <mat-icon>edit</mat-icon>
                      </button>
                    </div>
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
    `.payment-action{display:flex;align-items:center;gap:6px;justify-content:flex-start}`,
    `.payment-action button{width:32px;height:32px;line-height:32px}`,
    `.payment-action mat-icon{font-size:18px;width:18px;height:18px}`, 
    `table{width:100%;min-width:1180px}`,
    `@media(max-width:1100px){.filters{grid-template-columns:repeat(2,minmax(180px,1fr))}}`,
    `@media(max-width:640px){.filters{grid-template-columns:1fr}.filter-actions{justify-content:flex-start}}`
  ]
})
export class CommissionPaymentsPageComponent implements OnInit {
  loading = true;
  rows: CommissionPayment[] = [];

  readonly form = this.fb.group({
    producerName: [''],
    policyNumber: [''],
    agencyName: [''],
    carrierName: ['']
  });

  readonly columns = [
    'policyName',
    'commissionStatementId',
    'producerName',
    'agencyName',
    'carrierName',
    'netAmount',
    'rate',
    'commissionRatePct',
    'paymentAmount',
    'includedForPayment',
    'createdAt'
  ];

  readonly headers: Record<string, string> = {
    policyName: 'Póliza',
    commissionStatementId: 'Statement',
    producerName: 'Producer',
    agencyName: 'Agent',
    carrierName: 'Carrier',
    netAmount: 'Net amount',
    rate: 'Rate',
    commissionRatePct: 'Commission %',
    paymentAmount: 'Pago producer',
    includedForPayment: 'Incluido pago',
    createdAt: 'Fecha creación'
  };

  constructor(
    private readonly fb: FormBuilder,
    private readonly service: CommissionPaymentService,
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

  openRecalculateDialog(row: CommissionPayment): void {
    if (!row.id) {
      return;
    }

    this.service.getById(row.id).subscribe({
      next: (detail) => {
        const payment = this.mergePayment(row, detail);
        const dialogRef = this.dialog.open(CommissionPaymentRecalculateDialogComponent, {
          width: '760px',
          maxWidth: '96vw',
          data: { payment }
        });

        dialogRef.afterClosed().subscribe((updated?: CommissionPayment) => {
          if (!updated) {
            return;
          }

          this.rows = this.rows.map((item) =>
            item.id === row.id ? this.mergePayment(item, updated) : item
          );

          this.snackBar.open('Pago de comisión recalculado correctamente.', 'Cerrar', { duration: 2600 });
        });
      },
      error: () => {
        this.snackBar.open('No fue posible consultar el pago seleccionado.', 'Cerrar', { duration: 3200 });
      }
    });
  }

  displayValue(row: CommissionPayment, column: string): string {
    const value = (row as any)[column];

    if (value === null || value === undefined || value === '') {
      return '—';
    }

    if (typeof value === 'boolean') {
      return value ? 'Sí' : 'No';
    }

    if (['netAmount', 'rate', 'commissionRatePct', 'paymentAmount'].includes(column)) {
      return this.formatNumber(value);
    }

    if (column === 'createdAt') {
      return this.formatDate(value);
    }

    return String(value);
  }

  private mergePayment(current: CommissionPayment, updated: CommissionPayment): CommissionPayment {
    return {
      ...current,
      policyId: updated.policyId ?? current.policyId,
      policyName: updated.policyName ?? current.policyName,
      commissionStatementId: updated.commissionStatementId ?? current.commissionStatementId,
      producerId: updated.producerId ?? current.producerId,
      producerName: updated.producerName ?? current.producerName,
      agencyId: updated.agencyId ?? current.agencyId,
      agencyName: updated.agencyName ?? current.agencyName,
      carrierId: updated.carrierId ?? current.carrierId,
      carrierName: updated.carrierName ?? current.carrierName,
      netAmount: updated.netAmount ?? current.netAmount,
      rate: updated.rate ?? current.rate,
      commissionRatePct: updated.commissionRatePct ?? current.commissionRatePct,
      paymentAmount: updated.paymentAmount ?? current.paymentAmount,
      includedForPayment: updated.includedForPayment ?? current.includedForPayment,
      createdAt: updated.createdAt ?? current.createdAt
    };
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
        this.snackBar.open('No fue posible consultar los pagos de comisiones.', 'Cerrar', { duration: 3200 });
      }
    });
  }

  private formatNumber(value: unknown): string {
    const numberValue = Number(value);
    if (Number.isNaN(numberValue)) {
      return String(value);
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
