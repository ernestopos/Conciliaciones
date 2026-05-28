import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { CrudFieldConfig } from '../../../core/models/crud.models';
import { CrudServiceRegistry } from '../../../core/services/crud-service.registry';

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
    MatSelectModule
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
              <input *ngIf="field.type === 'email'" matInput type="email" [formControlName]="field.key" />
              <mat-select *ngIf="field.type === 'select'" [formControlName]="field.key">
                <mat-option *ngFor="let option of fieldOptions[field.key] ?? []" [value]="getOptionValue(field, option)">
                  {{ getOptionLabel(field, option) }}
                </mat-option>
              </mat-select>
              <input *ngIf="!field.type || field.type === 'text'" matInput type="text" [formControlName]="field.key" />
              <mat-error *ngIf="getControl(field.key)?.hasError('required')">Campo requerido</mat-error>
              <mat-error *ngIf="getControl(field.key)?.hasError('email')">Formato de correo inválido</mat-error>
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
export class CrudFormDialogComponent implements OnInit {
  fieldOptions: Record<string, Record<string, any>[]> = {};
  readonly form = this.fb.group(this.buildControls());

  constructor(
    private readonly fb: FormBuilder,
    private readonly serviceRegistry: CrudServiceRegistry,
    public readonly dialogRef: MatDialogRef<CrudFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public readonly data: CrudFormDialogData
  ) {}

  ngOnInit(): void {
    this.loadSelectOptions();
  }

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

  getOptionValue(field: CrudFieldConfig, option: Record<string, any>): unknown {
    return option[field.optionValueKey ?? 'id'];
  }

  getOptionLabel(field: CrudFieldConfig, option: Record<string, any>): string {
    const labelKey = field.optionLabelKey ?? 'name';
    const label = option[labelKey] ?? option['fullName'] ?? option['code'] ?? option['id'];
    return String(label ?? '');
  }

  private loadSelectOptions(): void {
    this.data.fields
      .filter((field) => field.type === 'select' && field.optionsResourceKey)
      .forEach((field) => {
        this.serviceRegistry.get(field.optionsResourceKey!).list().subscribe({
          next: (options) => {
            this.fieldOptions[field.key] = options;
          },
          error: () => {
            this.fieldOptions[field.key] = [];
          }
        });
      });
  }

  private buildControls(): Record<string, any> {
    return this.data.fields.reduce<Record<string, any>>((controls, field) => {
      const initialValue = this.data.initialValue?.[field.key] ?? (field.type === 'checkbox' ? false : '');
      const validators = [];
      if (field.required) {
        validators.push(Validators.required);
      }
      if (field.type === 'email') {
        validators.push(Validators.email);
      }
      controls[field.key] = [initialValue, validators];
      return controls;
    }, {});
  }
}
