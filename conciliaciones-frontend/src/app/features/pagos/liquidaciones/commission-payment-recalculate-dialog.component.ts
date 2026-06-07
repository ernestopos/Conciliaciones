import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CommissionPayment, RecalculateCommissionPaymentRequest } from '../../../models/commission-payment.model';
import { CommissionPaymentService } from '../../../services/commission-payment.service';

@Component({
  selector: 'app-commission-payment-recalculate-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatSnackBarModule
  ],
  template: `
    <h2 mat-dialog-title>Ajustar pago de comisión</h2>

    <mat-dialog-content>
      <div class="summary">
        <div>
          <span class="summary-label">Póliza</span>
          <strong>{{ data.payment.policyName || '—' }}</strong>
        </div>
        <div>
          <span class="summary-label">Producer</span>
          <strong>{{ data.payment.producerName || '—' }}</strong>
        </div>
        <div>
          <span class="summary-label">Pago actual</span>
          <strong>{{ formatNumber(data.payment.paymentAmount) }}</strong>
        </div>
      </div>

      <form [formGroup]="form" class="recalculate-form">
        <mat-form-field appearance="outline">
          <mat-label>Net amount</mat-label>
          <input matInput type="number" min="0.01" step="0.01" formControlName="netAmount" />
          <mat-error>Debe ingresar un valor mayor a cero</mat-error>
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Rate</mat-label>
          <input matInput type="number" min="0.01" step="0.01" formControlName="rate" />
          <mat-error>Debe ingresar un valor mayor a cero</mat-error>
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Commission %</mat-label>
          <input matInput type="number" min="0.01" step="0.01" formControlName="commissionRatePct" />
          <mat-error>Debe ingresar un valor mayor a cero</mat-error>
        </mat-form-field>
      </form>

      <div class="preview">
        <span>Nuevo pago producer</span>
        <strong>{{ formatNumber(calculatedPayment) }}</strong>
      </div>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button type="button" [disabled]="saving" (click)="dialogRef.close()">Cancelar</button>
      <button mat-raised-button color="primary" type="button" [disabled]="form.invalid || saving" (click)="save()">
        <mat-icon>calculate</mat-icon>
        {{ saving ? 'Guardando...' : 'Guardar cálculo' }}
      </button>
    </mat-dialog-actions>
  `,
  styles: [
    `.summary{display:grid;grid-template-columns:repeat(3,minmax(140px,1fr));gap:12px;margin-bottom:16px}`,
    `.summary>div{background:rgba(0,0,0,.03);border-radius:10px;padding:10px 12px}`,
    `.summary-label{display:block;font-size:12px;opacity:.72;margin-bottom:4px}`,
    `.recalculate-form{display:grid;grid-template-columns:repeat(3,minmax(160px,1fr));gap:12px;padding-top:4px}`,
    `.preview{display:flex;justify-content:space-between;align-items:center;border-top:1px solid rgba(0,0,0,.12);margin-top:8px;padding-top:14px;font-size:16px}`,
    `.preview strong{font-size:20px}`,
    `@media(max-width:760px){.summary,.recalculate-form{grid-template-columns:1fr}}`
  ]
})
export class CommissionPaymentRecalculateDialogComponent {
  saving = false;

  readonly form = this.fb.group({
    netAmount: [this.toNumber(this.data.payment.netAmount), [Validators.required, Validators.min(0.01)]],
    rate: [this.toNumber(this.data.payment.rate), [Validators.required, Validators.min(0.01)]],
    commissionRatePct: [this.toNumber(this.data.payment.commissionRatePct), [Validators.required, Validators.min(0.01)]]
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly service: CommissionPaymentService,
    private readonly snackBar: MatSnackBar,
    public readonly dialogRef: MatDialogRef<CommissionPaymentRecalculateDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public readonly data: { payment: CommissionPayment }
  ) {}

  get calculatedPayment(): number {
    const netAmount = this.toNumber(this.form.controls.netAmount.value);
    const rate = this.toNumber(this.form.controls.rate.value);
    const commissionRatePct = this.toNumber(this.form.controls.commissionRatePct.value);

    if (netAmount <= 0 || rate <= 0 || commissionRatePct <= 0) {
      return 0;
    }

    return Math.round(((netAmount * rate * commissionRatePct) / 10000) * 100) / 100;
  }

  save(): void {
    if (this.form.invalid || !this.data.payment.id) {
      this.form.markAllAsTouched();
      return;
    }

    const request: RecalculateCommissionPaymentRequest = {
      netAmount: this.toNumber(this.form.controls.netAmount.value),
      rate: this.toNumber(this.form.controls.rate.value),
      commissionRatePct: this.toNumber(this.form.controls.commissionRatePct.value)
    };

    this.saving = true;

    this.service.recalculate(this.data.payment.id, request).subscribe({
      next: (payment) => {
        this.saving = false;
        this.dialogRef.close(payment);
      },
      error: () => {
        this.saving = false;
        this.snackBar.open('No fue posible recalcular el pago de la comisión.', 'Cerrar', { duration: 3200 });
      }
    });
  }

  formatNumber(value: unknown): string {
    const numberValue = Number(value ?? 0);
    if (Number.isNaN(numberValue)) {
      return '0.00';
    }
    return new Intl.NumberFormat('en-US', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(numberValue);
  }

  private toNumber(value: unknown): number {
    const numberValue = Number(value);
    return Number.isNaN(numberValue) ? 0 : numberValue;
  }
}
