import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { AuthProviderPort } from '../ports/auth-provider.port';
import { AuthSession, AuthUser, LoginCredentials, LoginResult, Role } from '../models/auth.models';
import { clearAuthSession, readAuthSession, saveAuthSession } from './auth-storage';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class BackendAuthProvider extends AuthProviderPort {
  constructor(private readonly http: HttpClient) {
    super();
  }

  login(credentials: LoginCredentials): Observable<LoginResult> {
    return this.http.post<unknown>(`${environment.api.auth}/auth/login`, credentials).pipe(
      map((response) => this.handleSuccess(response, credentials.username)),
      catchError((error: HttpErrorResponse) => of(this.handleError(error)))
    );
  }

  logout(): void {
    clearAuthSession();
  }

  isAuthenticated(): boolean {
    return Boolean(readAuthSession()?.token);
  }

  getCurrentUser(): AuthUser | null {
    return readAuthSession()?.user ?? null;
  }

  private handleSuccess(response: unknown, username: string): LoginResult {
    const token = this.extractToken(response);
    if (!token) {
      return {
        success: false,
        errorMessage: 'La autenticación respondió sin token válido.'
      };
    }

    const payload = this.decodeJwtPayload(token);
    const user = this.extractUser(response, payload, username);
    const refreshToken = this.extractRefreshToken(response);
    const expiresIn = this.extractExpiresIn(response, payload);
    const expiresAt = this.extractExpiresAt(response, payload, expiresIn);

    const session: AuthSession = {
      token,
      refreshToken,
      user,
      expiresIn,
      expiresAt
    };

    saveAuthSession(session);

    return {
      success: true,
      token,
      refreshToken,
      user,
      expiresIn,
      expiresAt
    };
  }

  private handleError(error: HttpErrorResponse): LoginResult {
    clearAuthSession();

    if (error.status === 401) {
      return {
        success: false,
        errorMessage: 'Credenciales inválidas. Verifique usuario y contraseña.'
      };
    }

    if (error.status === 403) {
      return {
        success: false,
        errorMessage: 'No tiene permisos para ingresar a la aplicación.'
      };
    }

    const backendMessage =
      (error.error as any)?.message ??
      (error.error as any)?.errorMessage ??
      (error.error as any)?.error_description;

    return {
      success: false,
      errorMessage: backendMessage ?? 'No fue posible iniciar sesión en este momento.'
    };
  }

  private extractToken(response: any): string | null {
    const candidates = [
      response?.accessToken,
      response?.token,
      response?.jwt,
      response?.idToken,
      response?.data?.accessToken,
      response?.data?.token,
      response?.result?.accessToken,
      response?.result?.token
    ];

    return candidates.find((candidate) => typeof candidate === 'string' && candidate.trim().length > 0) ?? null;
  }

  private extractRefreshToken(response: any): string | undefined {
    const candidates = [
      response?.refreshToken,
      response?.refresh_token,
      response?.data?.refreshToken,
      response?.data?.refresh_token,
      response?.result?.refreshToken,
      response?.result?.refresh_token
    ];

    return candidates.find((candidate) => typeof candidate === 'string' && candidate.trim().length > 0);
  }

  private extractExpiresIn(response: any, payload: any): number | undefined {
    const explicit = response?.expiresIn ?? response?.expires_in ?? response?.data?.expiresIn ?? response?.data?.expires_in;
    if (typeof explicit === 'number') {
      return explicit;
    }

    if (typeof payload?.exp === 'number' && typeof payload?.iat === 'number') {
      return payload.exp - payload.iat;
    }

    return undefined;
  }

  private extractExpiresAt(response: any, payload: any, expiresIn?: number): number | undefined {
    const explicit = response?.expiresAt ?? response?.expires_at ?? response?.data?.expiresAt ?? response?.data?.expires_at;
    if (typeof explicit === 'number') {
      return explicit > 10_000_000_000 ? explicit : explicit * 1000;
    }

    if (typeof payload?.exp === 'number') {
      return payload.exp * 1000;
    }

    if (typeof expiresIn === 'number') {
      return Date.now() + expiresIn * 1000;
    }

    return undefined;
  }

  private extractUser(response: any, payload: any, username: string): AuthUser {
    const responseUser = response?.user ?? response?.data?.user ?? response?.result?.user;
    if (responseUser) {
      return {
        id: Number(responseUser.id ?? payload?.sub ?? 0),
        name: responseUser.name ?? responseUser.fullName ?? responseUser.username ?? payload?.name ?? username,
        email: responseUser.email ?? payload?.email ?? '',
        roles: this.extractRoles(responseUser.roles ?? payload?.realm_access?.roles ?? payload?.roles),
        permissions: responseUser.permissions ?? []
      };
    }

    return {
      id: Number(payload?.sub ?? 0),
      name: payload?.name ?? payload?.preferred_username ?? username,
      email: payload?.email ?? '',
      roles: this.extractRoles(payload?.realm_access?.roles ?? payload?.roles),
      permissions: []
    };
  }

  private extractRoles(raw: unknown): Role[] {
    if (!raw) {
      return [];
    }

    if (Array.isArray(raw)) {
      return raw.map((item, index) => {
        if (typeof item === 'string') {
          return { code: item, name: item };
        }
        return {
          code: String((item as any).code ?? (item as any).name ?? `ROLE_${index + 1}`),
          name: String((item as any).name ?? (item as any).code ?? `Rol ${index + 1}`)
        };
      });
    }

    return [];
  }

  private decodeJwtPayload(token: string): any {
    try {
      const payload = token.split('.')[1];
      if (!payload) {
        return {};
      }

      const normalized = payload.replace(/-/g, '+').replace(/_/g, '/');
      const padded = normalized.padEnd(
        normalized.length + (4 - (normalized.length % 4)) % 4,
        '='
      );
      const decoded = atob(padded);
      return JSON.parse(decoded);
    } catch {
      return {};
    }
  }
}
