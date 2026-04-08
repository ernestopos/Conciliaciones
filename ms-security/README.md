# ms-security

Microservicio de seguridad para la plataforma **Conciliaciones**.

## Responsabilidad

Este micro encapsula la integración con Keycloak y expone APIs funcionales para:

- autenticación
- refresh token
- logout
- consulta del usuario autenticado
- administración de usuarios
- administración de roles
- auditoría de seguridad

## Stack

- Java 21
- Spring Boot 3.3.x
- Spring Security
- OAuth2 Resource Server
- OpenAPI / Swagger
- PostgreSQL

## Endpoints base

- `POST /api/security/auth/login`
- `POST /api/security/auth/refresh`
- `POST /api/security/auth/logout`
- `GET /api/security/me`
- `GET /api/security/me/roles`
- `GET /api/security/me/permissions`
- `POST /api/security/users`
- `GET /api/security/users`
- `GET /api/security/users/{userId}`
- `PUT /api/security/users/{userId}`
- `PATCH /api/security/users/{userId}/status`
- `PATCH /api/security/users/{userId}/reset-password`
- `POST /api/security/users/{userId}/roles`
- `GET /api/security/roles`
- `POST /api/security/roles`
- `GET /api/security/roles/{roleName}`
- `PUT /api/security/roles/{roleName}`
- `POST /api/security/roles/{roleName}/permissions`
- `GET /api/security/audit`
- `GET /api/security/audit/{eventId}`

## Swagger

- UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI: `http://localhost:8080/v3/api-docs`

## Ejecución local

```bash
mvn spring-boot:run
```

## Notas

Este proyecto es un **cascarón funcional/base**. Incluye:

- diseño de paquetes
- contratos REST
- configuración de seguridad
- servicios stub para avanzar en la implementación

La integración real con Keycloak Admin API y token endpoint puede conectarse en una siguiente iteración.
