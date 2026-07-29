import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BaseCrudHttpService } from '../core/services/base-crud-http.service';
import { Policy } from '../models/policy.model';
import { map, Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { normalizeCollectionResponse } from '../core/services/api-response.utils';
import { DonutChartValue } from '../models/donut-chart.model';

@Injectable({ providedIn: 'root' })
export class PolicyService extends BaseCrudHttpService<Policy> {
  constructor(http: HttpClient) {
    super(http, '/policies');
  }

  override list(): Observable<Policy[]> {
    const params = new HttpParams()
      .set('page', '0')
      .set('size', '1000')
      .set('sort', 'id,asc');

    return this.http
      .get<unknown>(`${environment.api.core}/policies`, { params })
      .pipe(
        map((response) =>
          normalizeCollectionResponse<Policy>(response)
        )
      );
  }

  getPoliciesChart(): Observable<DonutChartValue[]> {
    return this.http.get<DonutChartValue[]>(
      `${environment.api.core}/policies/charPolicyCreate`
    );
  }
}