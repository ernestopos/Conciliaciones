import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { catchError, forkJoin, of } from 'rxjs';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { DonutSummaryChartComponent } from '../../shared/components/donut-summary-chart/donut-summary-chart.component';
import { DonutChartItem, DonutChartValue } from '../../models/donut-chart.model';
import { SourceFileService } from '../../services/source-file.service';
import { PolicyService } from '../../services/policy.service';
import { ReconciliationCaseService } from '../../services/reconciliation-case.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, PageHeaderComponent, DonutSummaryChartComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  private readonly chartColors = [
    '#2879f0',
    '#20b968',
    '#ff9d00',
    '#8144d6',
    '#f44336',
    '#16b7c5',
    '#4b5563',
    '#ec4899',
    '#0ea5e9',
    '#84cc16'
  ];

  fileChartItems: DonutChartItem[] = [];
  policyChartItems: DonutChartItem[] = [];
  reconciliationCaseChartItems: DonutChartItem[] = [];

  loading = true;
  fileChartError = '';
  policyChartError = '';
  reconciliationCaseChartError = '';

  constructor(
    private readonly sourceFileService: SourceFileService,
    private readonly policyService: PolicyService,
    private readonly reconciliationCaseService: ReconciliationCaseService
  ) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  private loadDashboard(): void {
    this.loading = true;
    this.fileChartError = '';
    this.policyChartError = '';
    this.reconciliationCaseChartError = '';

    const files$ = this.sourceFileService.getFileUploadsChart().pipe(
      catchError((error) => {
        console.error('Error loading file uploads chart', error);
        this.fileChartError = 'No fue posible cargar la distribución de archivos.';
        return of([] as DonutChartValue[]);
      })
    );

    const policies$ = this.policyService.getPoliciesChart().pipe(
      catchError((error) => {
        console.error('Error loading policies chart', error);
        this.policyChartError = 'No fue posible cargar la distribución de pólizas.';
        return of([] as DonutChartValue[]);
      })
    );

    const reconciliationCases$ = this.reconciliationCaseService
      .getReconciliationCasesChart()
      .pipe(
        catchError((error) => {
          console.error('Error loading reconciliation cases chart', error);
          this.reconciliationCaseChartError =
            'No fue posible cargar la distribución de casos de conciliación.';
          return of([] as DonutChartValue[]);
        })
      );

    forkJoin({
      files: files$,
      policies: policies$,
      reconciliationCases: reconciliationCases$
    }).subscribe(({ files, policies, reconciliationCases }) => {
      this.fileChartItems = this.mapChartItems(files);
      this.policyChartItems = this.mapChartItems(policies);
      this.reconciliationCaseChartItems = this.mapChartItems(reconciliationCases);
      this.loading = false;
    });
  }

  private mapChartItems(values: DonutChartValue[] | null | undefined): DonutChartItem[] {
    return (values ?? [])
      .filter((value) => value?.statusName && Number(value.total) > 0)
      .map((value, index) => ({
        statusName: value.statusName,
        total: Number(value.total),
        color: this.chartColors[index % this.chartColors.length]
      }));
  }
}
