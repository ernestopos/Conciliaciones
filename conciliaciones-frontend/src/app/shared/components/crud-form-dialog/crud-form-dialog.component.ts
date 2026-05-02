import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { CrudFieldConfig } from '../../../core/models/crud.models';

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
    MatInputModule
  ],
  template: `
    <h2 mat-dialog-title>{{ data.title }}</h2>
    <mat-dialog-content>
      <form [formGroup]="form" class="crud-form">
        <ng-container *ngFor="let field of data.fields">
          <mat-checkbox *ngIf="field.type === 'checkbox'; else standardField" [formControlName]="field.key">
            {{ field.label }}
          </mat-checkbox>

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

  constructor(
    private readonly fb: FormBuilder,
    public readonly dialogRef: MatDialogRef<CrudFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public readonly data: CrudFormDialogData
  ) {}

  getControl(key: string) {
    return this.form.get(key);
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
}
