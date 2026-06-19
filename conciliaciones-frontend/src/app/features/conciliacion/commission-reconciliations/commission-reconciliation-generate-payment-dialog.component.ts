import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CommissionReconciliation, GenerateCommissionPaymentRequest } from '../../../models/commission-reconciliation.model';
import { Parameter } from '../../../models/parameter.model';
import { CommissionReconciliationService } from '../../../services/commission-reconciliation.service';
import { ParameterService } from '../../../services/parameter.service';

@Component({
  selector: 'app-commission-reconciliation-generate-payment-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatSelectModule,
    MatSnackBarModule
  ],
  template: `
    <h2 mat-dialog-title>Corregir caso y generar pago</h2>

    <mat-dialog-content>
      <div class="summary">
        <div>
          <span class="summary-label">Póliza</span>
          <strong>{{ data.reconciliation.policyNumber || '—' }}</strong>
        </div>
        <div>
          <span class="summary-label">Estado actual</span>
          <strong>{{ data.reconciliation.policyStatusName || '—' }}</strong>
        </div>
        <div>
          <span class="summary-label">Motivo</span>
          <strong>{{ data.reconciliation.reconciliationReason || '—' }}</strong>
        </div>
      </div>

      <form [formGroup]="form" class="generate-payment-form">
        <mat-form-field appearance="outline">
          <mat-label>Estado póliza</mat-label>
          <mat-select formControlName="policyStatusId">
            <mat-option [value]="null">Sin cambio</mat-option>
            <mat-option *ngFor="let status of statusOptions" [value]="status.id">
              {{ status.name }}
            </mat-option>
          </mat-select>
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Net amount</mat-label>
          <input matInput type="number" min="0" step="0.01" formControlName="netAmount" />
          <mat-error>Debe ingresar un valor mayor o igual a cero</mat-error>
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
        <span>Pago producer estimado</span>
        <strong>{{ formatNumber(calculatedPayment) }}</strong>
      </div>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button type="button" [disabled]="saving" (click)="dialogRef.close()">Cancelar</button>
      <button mat-raised-button color="primary" type="button" [disabled]="form.invalid || saving" (click)="save()">
        <mat-icon>payments</mat-icon>
        {{ saving ? 'Generando...' : 'Generar pago' }}
      </button>
    </mat-dialog-actions>
  `,
  styles: [
    `.summary{display:grid;grid-template-columns:repeat(3,minmax(150px,1fr));gap:12px;margin-bottom:16px}`,
    `.summary>div{background:rgba(0,0,0,.03);border-radius:10px;padding:10px 12px}`,
    `.summary-label{display:block;font-size:12px;opacity:.72;margin-bottom:4px}`,
    `.generate-payment-form{display:grid;grid-template-columns:repeat(2,minmax(180px,1fr));gap:12px;padding-top:4px}`,
    `.preview{display:flex;justify-content:space-between;align-items:center;border-top:1px solid rgba(0,0,0,.12);margin-top:8px;padding-top:14px;font-size:16px}`,
    `.preview strong{font-size:20px}`,
    `@media(max-width:760px){.summary,.generate-payment-form{grid-template-columns:1fr}}`
  ]
})
export class CommissionReconciliationGeneratePaymentDialogComponent implements OnInit {
  saving = false;
  statusOptions: Parameter[] = [];

  readonly form = this.fb.group({
    policyStatusId: [this.data.reconciliation.policyStatusId ?? null],
    netAmount: [this.toNumber(this.data.reconciliation.netAmount), [Validators.required, Validators.min(0)]],
    rate: [this.toNumber(this.data.reconciliation.rate), [Validators.required, Validators.min(0.01)]],
    commissionRatePct: [this.toNumber(this.data.reconciliation.commissionRatePct), [Validators.required, Validators.min(0.01)]]
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly service: CommissionReconciliationService,
    private readonly parameterService: ParameterService,
    private readonly snackBar: MatSnackBar,
    public readonly dialogRef: MatDialogRef<CommissionReconciliationGeneratePaymentDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public readonly data: { reconciliation: CommissionReconciliation }
  ) {}

  ngOnInit(): void {
    this.parameterService.list().subscribe({
      next: (parameters) => {
        this.statusOptions = parameters
          .filter((parameter) => this.matchesGroup(parameter, ['POLICY_STATUS', 'STATUS_POLICY', 'ESTADOS_POLIZA']))
          .filter((parameter) => parameter.active !== false)
          .sort((a, b) => (a.sortOrder ?? 0) - (b.sortOrder ?? 0));
      },
      error: () => {
        this.statusOptions = [];
        this.snackBar.open('No fue posible cargar los estados de póliza.', 'Cerrar', { duration: 3200 });
      }
    });
  }

  get calculatedPayment(): number {
    const netAmount = this.toNumber(this.form.controls.netAmount.value);
    const rate = this.toNumber(this.form.controls.rate.value);
    const commissionRatePct = this.toNumber(this.form.controls.commissionRatePct.value);

    if (netAmount < 0 || rate <= 0 || commissionRatePct <= 0) {
      return 0;
    }

    return Math.round(((netAmount * rate * commissionRatePct) / 10000) * 100) / 100;
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    const request: GenerateCommissionPaymentRequest = {
      policyStatusId: raw.policyStatusId ?? null,
      netAmount: this.toNumber(raw.netAmount),
      rate: this.toNumber(raw.rate),
      commissionRatePct: this.toNumber(raw.commissionRatePct)
    };

    this.saving = true;

    this.service.generatePayment(this.data.reconciliation.commissionStatementItemId, request).subscribe({
      next: (response) => {
        this.saving = false;
        this.dialogRef.close(response);
      },
      error: () => {
        this.saving = false;
        this.snackBar.open('No fue posible generar el pago de la comisión.', 'Cerrar', { duration: 3600 });
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

  private matchesGroup(parameter: Parameter, groups: string[]): boolean {
    const group = parameter.parameterGroup?.trim().toUpperCase();
    return groups.includes(group);
  }
}
