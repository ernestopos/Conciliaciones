import { Injectable } from '@angular/core';
import { HttpBackend, HttpClient } from '@angular/common/http';
import { Observable, finalize, map, shareReplay, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthSession } from '../models/auth.models';
import { getJwtExpirationMs, readStoredAuthSession, saveAuthSession } from './auth-storage';

@Injectable({ providedIn: 'root' })
export class AuthRefreshService {
  private readonly http: HttpClient;
  private refreshInProgress$?: Observable<AuthSession>;

  constructor(httpBackend: HttpBackend) {
    // HttpClient sin interceptores para evitar ciclos cuando se renueva el token.
    this.http = new HttpClient(httpBackend);
  }

  refreshSession(): Observable<AuthSession> {
    const currentSession = readStoredAuthSession();
    if (!currentSession?.refreshToken) {
      return throwError(() => new Error('No existe refresh token para renovar la sesión.'));
    }

    if (!this.refreshInProgress$) {
      this.refreshInProgress$ = this.http
        .post<unknown>(`${environment.api.auth}/auth/refresh`, {
          refreshToken: currentSession.refreshToken
        })
        .pipe(
          map((response) => this.buildSession(response, currentSession)),
          map((session) => {
            saveAuthSession(session);
            return session;
          }),
          shareReplay(1),
          finalize(() => {
            this.refreshInProgress$ = undefined;
          })
        );
    }

    return this.refreshInProgress$;
  }

  private buildSession(response: any, currentSession: AuthSession): AuthSession {
    const token = this.extractToken(response) ?? currentSession.token;
    const refreshToken = this.extractRefreshToken(response) ?? currentSession.refreshToken;
    const expiresIn = this.extractExpiresIn(response, token);
    const expiresAt = this.extractExpiresAt(response, token, expiresIn);

    return {
      ...currentSession,
      token,
      refreshToken,
      expiresIn,
      expiresAt
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

  private extractExpiresIn(response: any, token: string): number | undefined {
    const explicit = response?.expiresIn ?? response?.expires_in ?? response?.data?.expiresIn ?? response?.data?.expires_in;
    if (typeof explicit === 'number') {
      return explicit;
    }

    const payload = this.decodeJwtPayload(token);
    if (typeof payload?.exp === 'number' && typeof payload?.iat === 'number') {
      return payload.exp - payload.iat;
    }

    return undefined;
  }

  private extractExpiresAt(response: any, token: string, expiresIn?: number): number | undefined {
    const explicit = response?.expiresAt ?? response?.expires_at ?? response?.data?.expiresAt ?? response?.data?.expires_at;
    if (typeof explicit === 'number') {
      return explicit > 10_000_000_000 ? explicit : explicit * 1000;
    }

    const jwtExpirationMs = getJwtExpirationMs(token);
    if (jwtExpirationMs) {
      return jwtExpirationMs;
    }

    if (typeof expiresIn === 'number') {
      return Date.now() + expiresIn * 1000;
    }

    return undefined;
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
      return JSON.parse(atob(padded));
    } catch {
      return {};
    }
  }
}
