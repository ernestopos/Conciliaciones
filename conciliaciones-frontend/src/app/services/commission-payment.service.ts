import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { normalizeCollectionResponse } from '../core/services/api-response.utils';
import { CommissionPayment, CommissionPaymentFilters, CreateManualCommissionPaymentRequest, RecalculateCommissionPaymentRequest } from '../models/commission-payment.model';

@Injectable({ providedIn: 'root' })
export class CommissionPaymentService {
  private readonly resourceUrl = `${environment.api.core}/commission-payments`;

  constructor(private readonly http: HttpClient) {}

  list(filters: CommissionPaymentFilters = {}): Observable<CommissionPayment[]> {
    let params = new HttpParams();

    Object.entries(filters).forEach(([key, value]) => {
      const normalized = value?.trim();
      if (normalized) {
        params = params.set(key, normalized);
      }
    });

    return this.http
      .get<unknown>(this.resourceUrl, { params })
      .pipe(map((response) => normalizeCollectionResponse<CommissionPayment>(response)));
  }
  getById(id: number): Observable<CommissionPayment> {
    return this.http.get<CommissionPayment>(`${this.resourceUrl}/${id}`);
  }

  recalculate(id: number, request: RecalculateCommissionPaymentRequest): Observable<CommissionPayment> {
    return this.http.put<CommissionPayment>(`${this.resourceUrl}/${id}/recalculate`, request);
  }

  createManual(request: CreateManualCommissionPaymentRequest): Observable<CommissionPayment> {
    return this.http.post<CommissionPayment>(`${this.resourceUrl}/manual`, request);
  }
}
