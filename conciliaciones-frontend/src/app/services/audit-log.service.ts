import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { normalizeCollectionResponse } from '../core/services/api-response.utils';
import { AuditLog } from '../models/audit-log.model';

export interface AuditLogFilter {
  entityName?: string;
  entityId?: string;
  actionId?: number | string | null;
  username?: string;
  from?: string;
  to?: string;
}

@Injectable({ providedIn: 'root' })
export class AuditLogService {
  private readonly baseUrl = `${environment.api.core}/audit-logs`;

  constructor(private readonly http: HttpClient) {}

  list(filter: AuditLogFilter = {}): Observable<AuditLog[]> {
    let params = new HttpParams().set('sort', 'eventTimestamp,desc');

    Object.entries(filter).forEach(([key, value]) => {
      if (value !== null && value !== undefined && String(value).trim() !== '') {
        params = params.set(key, String(value));
      }
    });

    return this.http
      .get<unknown>(this.baseUrl, { params })
      .pipe(map((response) => normalizeCollectionResponse<AuditLog>(response)));
  }

  getById(id: number | string): Observable<AuditLog> {
    return this.http.get<AuditLog>(`${this.baseUrl}/${id}`);
  }
}
