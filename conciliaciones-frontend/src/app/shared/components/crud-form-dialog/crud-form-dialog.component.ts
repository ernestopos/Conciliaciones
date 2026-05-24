import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { CrudFieldConfig } from '../../../core/models/crud.models';
import { LookupDialogComponent } from '../lookup-dialog/lookup-dialog.component';

interface CrudFormDialogData {
  title: string;
  fields: CrudFieldConfig[];
  initialValue?: Record<string, unknown>;
}

@Component({
  selector: 'app-crud-form-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatCheckboxModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule
  ],
  template: `
    <h2 mat-dialog-title>{{ data.title }}</h2>
    <mat-dialog-content>
      <form [formGroup]="form" class="crud-form">
        <ng-container *ngFor="let field of data.fields">
          <mat-checkbox *ngIf="field.type === 'checkbox'; else nonCheckboxField" [formControlName]="field.key">
            {{ field.label }}
          </mat-checkbox>

          <ng-template #nonCheckboxField>
            <mat-form-field *ngIf="field.type === 'lookup'; else standardField" appearance="outline">
              <mat-label>{{ field.label }}</mat-label>
              <input matInput type="text" [value]="getLookupDisplayValue(field)" readonly />
              <button mat-icon-button matSuffix type="button" aria-label="Buscar" (click)="openLookup(field)">
                <mat-icon>search</mat-icon>
              </button>
              <mat-error *ngIf="getControl(field.key)?.hasError('required')">Campo requerido</mat-error>
            </mat-form-field>

            <ng-template #standardField>
              <mat-form-field appearance="outline">
                <mat-label>{{ field.label }}</mat-label>
                <textarea *ngIf="field.type === 'textarea'" matInput [formControlName]="field.key" rows="3"></textarea>
                <input *ngIf="field.type === 'number'" matInput type="number" [formControlName]="field.key" />
                <input *ngIf="field.type === 'date'" matInput type="date" [formControlName]="field.key" />
                <input *ngIf="field.type === 'datetime-local'" matInput type="datetime-local" [formControlName]="field.key" />
                <input *ngIf="!field.type || field.type === 'text'" matInput type="text" [formControlName]="field.key" />
                <mat-error *ngIf="getControl(field.key)?.hasError('required')">Campo requerido</mat-error>
              </mat-form-field>
            </ng-template>
          </ng-template>
        </ng-container>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button type="button" (click)="dialogRef.close()">Cancelar</button>
      <button mat-raised-button color="primary" type="button" (click)="submit()">Guardar</button>
    </mat-dialog-actions>
  `,
  styles: [
    `.crud-form{display:grid;grid-template-columns:repeat(auto-fit,minmax(240px,1fr));gap:16px;padding-top:8px}`,
    `mat-checkbox{margin-top:8px}`
  ]
})
export class CrudFormDialogComponent {
  readonly form = this.fb.group(this.buildControls());
  private readonly lookupDisplayValues: Record<string, string> = this.buildLookupDisplayValues();

  constructor(
    private readonly fb: FormBuilder,
    private readonly dialog: MatDialog,
    public readonly dialogRef: MatDialogRef<CrudFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public readonly data: CrudFormDialogData
  ) {}

  getControl(key: string) {
    return this.form.get(key);
  }

  getLookupDisplayValue(field: CrudFieldConfig): string {
    return this.lookupDisplayValues[field.key] ?? '';
  }

  openLookup(field: CrudFieldConfig): void {
    if (!field.lookup) {
      return;
    }

    const dialogRef = this.dialog.open(LookupDialogComponent, {
      width: '860px',
      maxWidth: '96vw',
      data: { lookup: field.lookup }
    });

    dialogRef.afterClosed().subscribe((selected) => {
      if (!selected) {
        return;
      }

      const value = selected[field.lookup!.valueKey];
      const display = selected[field.lookup!.displayKey];
      this.form.get(field.key)?.setValue(value);
      this.lookupDisplayValues[field.key] = display ? String(display) : String(value ?? '');
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    const normalized = Object.fromEntries(
      Object.entries(raw).map(([key, value]) => {
        if (value === '') {
          return [key, null];
        }
        return [key, value];
      })
    );

    this.dialogRef.close(normalized);
  }

  private buildControls(): Record<string, any> {
    return this.data.fields.reduce<Record<string, any>>((controls, field) => {
      const initialValue = this.data.initialValue?.[field.key] ?? (field.type === 'checkbox' ? false : '');
      controls[field.key] = [initialValue, field.required ? [Validators.required] : []];
      return controls;
    }, {});
  }

  private buildLookupDisplayValues(): Record<string, string> {
    return this.data.fields.reduce<Record<string, string>>((values, field) => {
      if (field.type === 'lookup' && field.displayKey) {
        const displayValue = this.data.initialValue?.[field.displayKey];
        values[field.key] = displayValue ? String(displayValue) : '';
      }
      return values;
    }, {});
  }
}
