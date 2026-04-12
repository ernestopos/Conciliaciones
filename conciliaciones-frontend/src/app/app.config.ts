import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { routes } from './app.routes';
import { AUTH_PROVIDER } from './auth/services/auth.service';
import { BackendAuthProvider } from './auth/providers/backend-auth.provider';
import { authInterceptor } from './auth/interceptors/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideAnimations(),
    provideHttpClient(withInterceptors([authInterceptor])),
    {
      provide: AUTH_PROVIDER,
      useClass: BackendAuthProvider
    }
  ]
};
