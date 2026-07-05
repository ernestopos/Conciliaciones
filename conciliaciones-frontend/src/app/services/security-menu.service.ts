import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { normalizeCollectionResponse } from '../core/services/api-response.utils';
import { SecurityMenuTree } from '../models/security-menu.model';

@Injectable({ providedIn: 'root' })
export class SecurityMenuService {
  private readonly baseUrl = `${environment.api.core}/security/menus`;

  constructor(private readonly http: HttpClient) {}

  findTree(): Observable<SecurityMenuTree[]> {
    return this.http
      .get<unknown>(this.baseUrl)
      .pipe(map(response => normalizeCollectionResponse<SecurityMenuTree>(response)));
  }

  findAdminTree(): Observable<SecurityMenuTree[]> {
    return this.http
      .get<unknown>(`${this.baseUrl}/roles/admin`)
      .pipe(map(response => normalizeCollectionResponse<SecurityMenuTree>(response)));
  }

  findUserTree(username: string): Observable<SecurityMenuTree[]> {
    return this.http
      .get<unknown>(`${this.baseUrl}/users/${encodeURIComponent(username)}`)
      .pipe(map(response => normalizeCollectionResponse<SecurityMenuTree>(response)));
  }
}
