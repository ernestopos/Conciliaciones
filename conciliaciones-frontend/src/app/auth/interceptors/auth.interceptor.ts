import { HttpInterceptorFn } from '@angular/common/http';

/**
 * Interceptor preparado para futura integración real con Keycloak.
 *
 * TODO Keycloak:
 * - Obtener token desde KeycloakAuthProvider.
 * - Inyectar Authorization: Bearer <token> en requests privadas.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req);
};
