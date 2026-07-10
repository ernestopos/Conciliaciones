import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDatepicker, MatDatepickerModule } from '@angular/material/datepicker';
import { MAT_DATE_FORMATS, MatNativeDateModule } from '@angular/material/core';
import { MatInputModule } from '@angular/material/input';
import { DomSanitizer } from '@angular/platform-browser';
import { catchError, finalize, of, timeout } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state.component';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner/loading-spinner.component';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { Parameter } from '../../models/parameter.model';
import { ParameterService } from '../../services/parameter.service';
import { GeneratedReportPreview } from './models/report.model';
import { ReportService } from './services/report.service';

const REPORTS_WITH_YEAR = ['PaymentMonthlyForCarrier'];
const YEAR_DATE_FORMATS = {
  parse: {
    dateInput: 'yyyy'
  },
  display: {
    dateInput: { year: 'numeric' },
    monthYearLabel: { year: 'numeric' },
    dateA11yLabel: { year: 'numeric' },
    monthYearA11yLabel: { year: 'numeric' }
  }
};

@Component({
  selector: 'app-reports-page',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatInputModule,
    MatIconModule,
    MatProgressBarModule,
    MatSelectModule,
    MatSnackBarModule,
    EmptyStateComponent,
    LoadingSpinnerComponent,
    PageHeaderComponent
  ],
  template: `
    <div class="page-shell">
      <app-page-header
        title="Reportes"
        subtitle="Seleccione el reporte que desea generar y visualizar."
      />

      <mat-card class="report-card">
        <mat-card-content>
          <form [formGroup]="form" class="report-form" (ngSubmit)="executeReport()">
            <mat-form-field appearance="outline" class="report-select">
              <mat-label>Reporte</mat-label>
              <mat-select formControlName="reportCode" [disabled]="loadingReports || generating">
                <mat-option *ngFor="let report of reports; trackBy: trackReport" [value]="report.value">
                  {{ report.description || report.name }}
                </mat-option>
              </mat-select>
              <mat-hint>Grupo de parámetros: REPORTING_SERVICES</mat-hint>
            </mat-form-field>

            @if (requiresYear) {
              <mat-form-field appearance="outline" class="year-select">
                <mat-label>Año</mat-label>
                <input
                  matInput
                  readonly
                  [matDatepicker]="yearPicker"
                  formControlName="year"
                  placeholder="Seleccione el año"
                />
                <mat-datepicker-toggle matIconSuffix [for]="yearPicker"></mat-datepicker-toggle>
                <mat-datepicker
                  #yearPicker
                  startView="multi-year"
                  (yearSelected)="onYearSelected($event, yearPicker)"
                ></mat-datepicker>
                <mat-hint>Seleccione solo el año del reporte</mat-hint>
              </mat-form-field>
            }

            <button
              mat-raised-button
              color="primary"
              type="submit"
              [disabled]="form.invalid || loadingReports || generating"
            >
              <mat-icon>play_arrow</mat-icon>
              Ejecutar
            </button>

            <button
              mat-stroked-button
              color="primary"
              type="button"
              [disabled]="!preview || generating"
              (click)="downloadReport()"
            >
              <mat-icon>download</mat-icon>
              Descargar PDF
            </button>
          </form>
        </mat-card-content>

        @if (generating) {
          <mat-progress-bar mode="indeterminate" />
        }
      </mat-card>

      @if (loadingReports) {
        <app-loading-spinner />
      } @else if (!reports.length) {
        <app-empty-state
          title="Sin reportes configurados"
          description="No existen parámetros activos para el grupo REPORTING_SERVICES."
        />
      } @else if (!preview) {
        <app-empty-state
          title="Reporte pendiente"
          description="Seleccione un reporte y presione Ejecutar para visualizar el PDF."
        />
      } @else {
        <mat-card class="viewer-card">
          <mat-card-content>
            <iframe
              class="pdf-viewer"
              title="Vista previa del reporte"
              [src]="preview.safeUrl"
            ></iframe>
          </mat-card-content>
        </mat-card>
      }
    </div>
  `,
  providers: [
    { provide: MAT_DATE_FORMATS, useValue: YEAR_DATE_FORMATS }
  ],
  styles: [
    `.page-shell{display:block}`,
    `.report-card{margin-bottom:16px;border-radius:14px}`,
    `.report-form{display:flex;gap:12px;align-items:flex-start;flex-wrap:wrap}`,
    `.report-select{min-width:320px;flex:1}`,
    `.year-select{width:180px}`,
    `.report-form button{height:56px}`,
    `.viewer-card{border-radius:14px;overflow:hidden}`,
    `.viewer-card mat-card-content{padding:0}`,
    `.pdf-viewer{display:block;width:100%;height:calc(100vh - 265px);min-height:560px;border:0;background:#f5f5f5}`,
    `@media(max-width:720px){.report-select{min-width:100%}.year-select{width:100%}.report-form button{width:100%}.pdf-viewer{height:620px}}`
  ]
})
export class ReportsPageComponent implements OnInit, OnDestroy {
  readonly form = this.fb.group({
    reportCode: ['', Validators.required],
    year: [null as Date | null]
  });

  reports: Parameter[] = [];
  loadingReports = true;
  generating = false;
  requiresYear = false;
  preview?: GeneratedReportPreview;

  constructor(
    private readonly fb: FormBuilder,
    private readonly parameterService: ParameterService,
    private readonly reportService: ReportService,
    private readonly sanitizer: DomSanitizer,
    private readonly snackBar: MatSnackBar,
    private readonly cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.listenReportChanges();
    this.loadReports();
  }

  ngOnDestroy(): void {
    this.clearPreview();
  }

  trackReport(index: number, report: Parameter): number | string {
    return report.id ?? report.value ?? index;
  }

  onYearSelected(date: Date, datepicker: MatDatepicker<Date>): void {
    this.form.controls.year.setValue(date);
    datepicker.close();
  }

  executeReport(): void {
    const reportCode = this.form.controls.reportCode.value;
    if (!reportCode) {
      return;
    }

    const parameters = this.buildReportParameters(reportCode);

    this.generating = true;
    this.reportService.generatePdf(reportCode, parameters).subscribe({
      next: ({ blob, fileName }) => {
        this.clearPreview();
        const url = URL.createObjectURL(blob);
        this.preview = {
          blob,
          url,
          fileName,
          safeUrl: this.sanitizer.bypassSecurityTrustResourceUrl(url)
        };
        this.generating = false;
      },
      error: () => {
        this.generating = false;
        this.snackBar.open('No fue posible generar el reporte seleccionado.', 'Cerrar', { duration: 3500 });
      }
    });
  }

  downloadReport(): void {
    if (!this.preview) {
      return;
    }

    const link = document.createElement('a');
    link.href = this.preview.url;
    link.download = this.preview.fileName;
    link.click();
  }


  private listenReportChanges(): void {
    this.form.controls.reportCode.valueChanges.subscribe((reportCode) => {
      this.requiresYear = REPORTS_WITH_YEAR.includes(reportCode ?? '');
      const yearControl = this.form.controls.year;

      if (this.requiresYear) {
        yearControl.setValidators([Validators.required]);
      } else {
        yearControl.clearValidators();
        yearControl.setValue(null);
      }

      yearControl.updateValueAndValidity();
    });
  }

  private buildReportParameters(reportCode: string): Record<string, unknown> {
    const parameters: Record<string, unknown> = { limit: 100 };

    if (REPORTS_WITH_YEAR.includes(reportCode)) {
      const selectedYear = this.form.controls.year.value;
      parameters['REPORT_YEAR'] = selectedYear?.getFullYear();
    }

    return parameters;
  }

  private loadReports(): void {
    this.loadingReports = true;

    this.parameterService.findByGroup('REPORTING_SERVICES')
      .pipe(
        timeout(15000),
        catchError((error) => {
          console.error('Error consultando parámetros REPORTING_SERVICES', error);
          this.snackBar.open('No fue posible consultar la lista de reportes.', 'Cerrar', { duration: 3500 });
          return of([] as Parameter[]);
        }),
        finalize(() => {
          this.loadingReports = false;
          this.cdr.markForCheck();
        })
      )
      .subscribe((reports) => {
        this.reports = Array.isArray(reports) ? reports : [];

        if (this.reports.length === 1) {
          this.form.patchValue({ reportCode: this.reports[0].value });
        }
      });
  }

  private clearPreview(): void {
    if (this.preview?.url) {
      URL.revokeObjectURL(this.preview.url);
    }
    this.preview = undefined;
  }
}
