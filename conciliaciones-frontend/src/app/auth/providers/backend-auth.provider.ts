import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

import { AuthProviderPort } from '../ports/auth-provider.port';
import {
  AuthUser,
  LoginCredentials,
  LoginResult,
  Permission,
  Role
} from '../models/auth.models';

interface BackendLoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  roles: string[];
}

interface JwtPayload {
  sub?: string;
  preferred_username?: string;
  name?: string;
  email?: string;
  realm_access?: {
    roles?: string[];
  };
}

@Injectable()
export class BackendAuthProvider implements AuthProviderPort {
  private readonly loginUrl = `${environment.securityApiBaseUrl}/api/v1/auth/login`;

  constructor(private readonly http: HttpClient) {}

  login(credentials: LoginCredentials): Observable<LoginResult> {
    return this.http.post<BackendLoginResponse>(this.loginUrl, credentials).pipe(
      map((response) => {
        const payload = this.decodeJwt(response.accessToken);

        const roleCodes = payload?.realm_access?.roles ?? response.roles ?? [];

        const user: AuthUser = {
          id: 0,
          name: payload?.name ?? credentials.username,
          email: payload?.email ?? '',
          roles: this.mapRoles(roleCodes),
          permissions: this.mapPermissions(roleCodes)
        };

        return { response, user };
      }),
      tap(({ response, user }) => {
        localStorage.setItem('access_token', response.accessToken);
        localStorage.setItem('refresh_token', response.refreshToken);
        localStorage.setItem('auth_user', JSON.stringify(user));
      }),
      map(({ user }) => ({
        success: true,
        user
      })),
      catchError((error) => {
        let errorMessage = 'No fue posible iniciar sesión';

        if (error?.status === 401) {
          errorMessage = 'Usuario o contraseña inválidos';
        } else if (error?.status === 403) {
          errorMessage = 'Acceso no autorizado';
        } else if (error?.status === 0) {
          errorMessage = 'No se pudo conectar con el backend';
        } else if (error?.error?.message) {
          errorMessage = error.error.message;
        }

        return of({
          success: false,
          errorMessage
        });
      })
    );
  }

  logout(): void {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('auth_user');
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('access_token');
  }

  getCurrentUser(): AuthUser | null {
    const raw = localStorage.getItem('auth_user');
    return raw ? (JSON.parse(raw) as AuthUser) : null;
  }

  private decodeJwt(token: string): JwtPayload | null {
    try {
      const base64 = token.split('.')[1];
      const normalized = base64.replace(/-/g, '+').replace(/_/g, '/');
      const padded = normalized.padEnd(
        normalized.length + (4 - (normalized.length % 4)) % 4,
        '='
      );
      return JSON.parse(atob(padded)) as JwtPayload;
    } catch {
      return null;
    }
  }

  private mapRoles(roleCodes: string[]): Role[] {
    return roleCodes.map((code) => ({
      code,
      name: code
    }));
  }

  private mapPermissions(_roleCodes: string[]): Permission[] {
    return [];
  }
}