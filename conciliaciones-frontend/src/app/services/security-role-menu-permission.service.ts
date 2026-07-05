import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { normalizeCollectionResponse } from '../core/services/api-response.utils';
import {
  SaveSecurityRoleMenuPermissionRequest,
  SecurityRoleMenuPermission
} from '../models/security-role-menu-permission.model';

@Injectable({ providedIn: 'root' })
export class SecurityRoleMenuPermissionService {
  private readonly baseUrl = `${environment.api.core}/security/roles`;

  constructor(private readonly http: HttpClient) {}

  findByRoleId(roleId: number): Observable<SecurityRoleMenuPermission[]> {
    return this.http
      .get<unknown>(`${this.baseUrl}/${roleId}/menu-permissions`)
      .pipe(map(response => normalizeCollectionResponse<SecurityRoleMenuPermission>(response)));
  }

  save(roleId: number, request: SaveSecurityRoleMenuPermissionRequest): Observable<SecurityRoleMenuPermission[]> {
    return this.http
      .put<unknown>(`${this.baseUrl}/${roleId}/menu-permissions`, request)
      .pipe(map(response => normalizeCollectionResponse<SecurityRoleMenuPermission>(response)));
  }
}
