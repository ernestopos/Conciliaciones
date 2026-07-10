import { inject } from '@angular/core';
import { HttpErrorResponse, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { clearAuthSession, isSessionExpired, readStoredAuthSession } from '../services/auth-storage';
import { AuthRefreshService } from '../services/auth-refresh.service';
import { AuthSession } from '../models/auth.models';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const authRefreshService = inject(AuthRefreshService);
  const session = readStoredAuthSession();

  const isProtectedBackendRequest = isProtectedRequest(req.url);
  const isAuthRequest = isLoginRequest(req.url) || isRefreshRequest(req.url);

  const logoutAndRedirect = () => {
    clearAuthSession();
    router.navigate(['/auth/login'], {
      queryParams: {
        sessionExpired: 'true'
      }
    });
  };

  if (!isProtectedBackendRequest || isAuthRequest || !session?.token) {
    return next(req);
  }

  if (isSessionExpired(session)) {
    if (!session.refreshToken) {
      logoutAndRedirect();
      return throwError(() => new Error('La sesión expiró.'));
    }

    return authRefreshService.refreshSession().pipe(
      switchMap((refreshedSession) => next(addAuthorizationHeader(req, refreshedSession.token))),
      catchError((error: HttpErrorResponse) => {
        logoutAndRedirect();
        return throwError(() => error);
      })
    );
  }

  return next(addAuthorizationHeader(req, session.token)).pipe(
    catchError((error: HttpErrorResponse) => {
      if (!shouldTryRefresh(error, session)) {
        return throwError(() => error);
      }

      return authRefreshService.refreshSession().pipe(
        switchMap((refreshedSession) => next(addAuthorizationHeader(req, refreshedSession.token))),
        catchError((refreshError: HttpErrorResponse) => {
          logoutAndRedirect();
          return throwError(() => refreshError);
        })
      );
    })
  );
};

function addAuthorizationHeader(req: HttpRequest<unknown>, token: string): HttpRequest<unknown> {
  return req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });
}

function isProtectedRequest(url: string): boolean {
  return (
    url.startsWith(environment.api.core) ||
    url.startsWith(environment.api.auth) ||
    url.startsWith(environment.api.fileManagement) ||
    url.startsWith(environment.api.reporting)
  );
}

function isLoginRequest(url: string): boolean {
  return url.includes('/auth/login');
}

function isRefreshRequest(url: string): boolean {
  return url.includes('/auth/refresh');
}

function shouldTryRefresh(error: HttpErrorResponse, session: AuthSession): boolean {
  return Boolean(session.refreshToken && (error.status === 401 || error.status === 403));
}
