import { inject } from '@angular/core';
import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { clearAuthSession, getStoredAccessToken } from '../services/auth-storage';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const token = getStoredAccessToken();

  const isProtectedBackendRequest =
    req.url.startsWith(environment.api.core) ||
    req.url.startsWith(environment.api.fileManagement);

  const authReq = token && isProtectedBackendRequest
    ? req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      })
    : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (isProtectedBackendRequest && (error.status === 401 || error.status === 403)) {
        clearAuthSession();
        router.navigate(['/auth/login'], {
          queryParams: { sessionExpired: 'true' }
        });
      }

      return throwError(() => error);
    })
  );
};
