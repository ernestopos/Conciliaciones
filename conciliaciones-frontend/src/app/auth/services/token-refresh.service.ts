import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { interval, of } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

interface RefreshResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}

@Injectable({ providedIn: 'root' })
export class TokenRefreshService {
  private readonly refreshUrl = `${environment.api.auth}/api/v1/auth/refresh`;
  private readonly CHECK_INTERVAL_MS = 60000;
  private readonly MIN_REMAINING_SECONDS = 120;

  constructor(private readonly http: HttpClient) {}

  start(): void {
    interval(this.CHECK_INTERVAL_MS)
      .pipe(
        switchMap(() => {
          if (!this.shouldRefreshToken()) {
            return of(null);
          }

          const refreshToken = localStorage.getItem('refresh_token');

          if (!refreshToken) {
            return of(null);
          }

          return this.http
            .post<RefreshResponse>(this.refreshUrl, { refreshToken })
            .pipe(
              catchError((error) => {
                console.error('[AUTH] Error renovando token', error);
                this.clearSession();
                window.location.href = '/login';
                return of(null);
              })
            );
        })
      )
      .subscribe((response) => {
        if (!response) {
          return;
        }

        localStorage.setItem('access_token', response.accessToken);
        localStorage.setItem('refresh_token', response.refreshToken);
      });
  }

  private shouldRefreshToken(): boolean {
    const token = localStorage.getItem('access_token');

    if (!token) {
      return false;
    }

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expiration = payload.exp;

      if (!expiration) {
        return false;
      }

      const currentTime = Math.floor(Date.now() / 1000);
      const remainingSeconds = expiration - currentTime;

      return remainingSeconds <= this.MIN_REMAINING_SECONDS;
    } catch {
      return false;
    }
  }

  private clearSession(): void {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('auth_user');
  }
}
