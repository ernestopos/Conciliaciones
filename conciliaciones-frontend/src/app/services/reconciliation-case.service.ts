import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseCrudHttpService } from '../core/services/base-crud-http.service';
import { ReconciliationCase } from '../models/reconciliation-case.model';
import { environment } from '../../environments/environment';
import { DonutChartValue } from '../models/donut-chart.model';

@Injectable({ providedIn: 'root' })
export class ReconciliationCaseService extends BaseCrudHttpService<ReconciliationCase> {
  constructor(private readonly httpClient: HttpClient) {
    super(httpClient, '/reconciliation-cases');
  }

  reprocess(id: number | string): Observable<unknown> {
    return this.httpClient.post(`${environment.api.core}/reconciliation-cases/${id}/reprocess`, {});
  }

  getReconciliationCasesChart(): Observable<DonutChartValue[]> {
    return this.httpClient.get<DonutChartValue[]>(
      `${environment.api.core}/reconciliation-cases/charReconcilationCase`
    );
  }
}
