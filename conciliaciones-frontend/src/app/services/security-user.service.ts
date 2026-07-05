import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { normalizeCollectionResponse } from '../core/services/api-response.utils';
import { ConfigureSecurityUserRequest, ConfiguredSecurityUser, SecurityUser } from '../models/security-user.model';

@Injectable({ providedIn: 'root' })
export class SecurityUserService {
  private readonly authBaseUrl = `${environment.api.auth}/security/users`;
  private readonly coreBaseUrl = `${environment.api.core}/security/users`;

  constructor(private readonly http: HttpClient) {}

  findAll(): Observable<SecurityUser[]> {
    return this.findKeycloakUsers();
  }

  findKeycloakUsers(): Observable<SecurityUser[]> {
    return this.http.get<SecurityUser[]>(this.authBaseUrl);
  }

  findConfiguredUsers(): Observable<ConfiguredSecurityUser[]> {
    const params = new HttpParams()
      .set('page', '0')
      .set('size', '1000')
      .set('sort', 'username,asc');

    return this.http
      .get<unknown>(`${this.coreBaseUrl}/configured`, { params })
      .pipe(map(response => normalizeCollectionResponse<ConfiguredSecurityUser>(response)));
  }

  configureUser(request: ConfigureSecurityUserRequest): Observable<ConfiguredSecurityUser> {
    return this.http.post<ConfiguredSecurityUser>(`${this.coreBaseUrl}/configure`, request);
  }
}
