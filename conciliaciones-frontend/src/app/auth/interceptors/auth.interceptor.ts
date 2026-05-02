import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { getStoredAccessToken } from '../services/auth-storage';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = getStoredAccessToken();

  const isProtectedBackendRequest =
    req.url.startsWith(environment.api.core) ||
    req.url.startsWith(environment.api.fileManagement);

  if (!token || !isProtectedBackendRequest) {
    return next(req);
  }

  const authReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });

  return next(authReq);
};
