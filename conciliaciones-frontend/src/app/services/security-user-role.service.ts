import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { environment } from '../../environments/environment';
import { normalizeCollectionResponse } from '../core/services/api-response.utils';
import { SaveSecurityUserRoleRequest, SecurityUserRole } from '../models/security-user-role.model';

@Injectable({ providedIn: 'root' })
export class SecurityUserRoleService {
  private readonly baseUrl = `${environment.api.core}/security/user-roles`;

  constructor(private readonly http: HttpClient) {}

  list(active?: boolean): Observable<SecurityUserRole[]> {
    let params = new HttpParams();

    if (active !== undefined && active !== null) {
      params = params.set('active', String(active));
    }

    return this.http
      .get<unknown>(this.baseUrl, { params })
      .pipe(map(response => normalizeCollectionResponse<SecurityUserRole>(response)));
  }

  save(request: SaveSecurityUserRoleRequest): Observable<SecurityUserRole> {
    return this.http.post<SecurityUserRole>(this.baseUrl, request);
  }

  deleteByUserId(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/users/${userId}`);
  }
}
