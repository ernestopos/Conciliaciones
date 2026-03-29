# ms-security

Microservicio de seguridad con arquitectura hexagonal (ports & adapters) para Conciliaciones.

## Endpoints de demo

- `POST /auth/login`
- `GET /auth/validate`
- `GET /auth/roles`

## Swagger

- UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI: `http://localhost:8080/v3/api-docs`

## Integración Keycloak (conciliaciones-iac)

Variables principales:

- `KEYCLOAK_SERVER_URL` (default `http://localhost:8081`)
- `KEYCLOAK_REALM` (default `conciliaciones`)
- `KEYCLOAK_CLIENT_ID` (default `conciliaciones-orchestrator`)
- `KEYCLOAK_CLIENT_SECRET` (default `orchestrator-secret-2026`)

## Resilience4j

Configurado sobre el adapter de Keycloak con:

- retry (`resilience4j.retry.instances.keycloakClient`)
- circuit breaker (`resilience4j.circuitbreaker.instances.keycloakClient`)

## Auditoría

Tabla: `security_audit_log`

Campos clave de trazabilidad:

- `valor_antes` (JSONB)
- `valor_despues` (JSONB)

## Docker

```bash
docker build -t ms-security:local .
```
