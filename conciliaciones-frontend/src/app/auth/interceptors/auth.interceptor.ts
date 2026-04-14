import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { getStoredAccessToken } from '../services/auth-storage';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = getStoredAccessToken();
  const isCoreRequest = req.url.startsWith(environment.api.core);

  if (!token || !isCoreRequest) {
    return next(req);
  }

  const authReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });

  return next(authReq);
};
