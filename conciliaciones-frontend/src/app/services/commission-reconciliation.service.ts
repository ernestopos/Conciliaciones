import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { normalizeCollectionResponse } from '../core/services/api-response.utils';
import {
  CommissionReconciliation,
  CommissionReconciliationFilters,
  GenerateCommissionPaymentRequest
} from '../models/commission-reconciliation.model';

@Injectable({ providedIn: 'root' })
export class CommissionReconciliationService {
  private readonly resourceUrl = `${environment.api.core}/commission-reconciliations`;

  constructor(private readonly http: HttpClient) {}

  list(filters: CommissionReconciliationFilters = {}): Observable<CommissionReconciliation[]> {
    let params = new HttpParams();

    Object.entries(filters).forEach(([key, value]) => {
      const normalized = value?.trim();
      if (normalized) {
        params = params.set(key, normalized);
      }
    });

    return this.http
      .get<unknown>(this.resourceUrl, { params })
      .pipe(map((response) => normalizeCollectionResponse<CommissionReconciliation>(response)));
  }

  getById(commissionStatementItemId: number): Observable<CommissionReconciliation> {
    return this.http.get<CommissionReconciliation>(`${this.resourceUrl}/${commissionStatementItemId}`);
  }

  generatePayment(
    commissionStatementItemId: number,
    request: GenerateCommissionPaymentRequest
  ): Observable<CommissionReconciliation> {
    return this.http.post<CommissionReconciliation>(
      `${this.resourceUrl}/${commissionStatementItemId}/generate-payment`,
      request
    );
  }
}
