# ms-file-management

Cascarón base del microservicio de gestión de archivos para la plataforma Conciliaciones.

## Stack
- Java 21
- Spring Boot 3.3.x
- Arquitectura hexagonal
- PostgreSQL
- Configuración en `.properties`
- OpenAPI / Swagger
- JWT / Keycloak como resource server

## Dependencias compartidas
Este micro integra los módulos compartidos:
- `com.conciliaciones:conciliaciones-domain-model:1.0.0-SNAPSHOT`
- `com.conciliaciones:conciliaciones-persistence-jpa:1.0.0-SNAPSHOT`

Antes de compilar este micro debes tener instalado en tu repositorio local el proyecto `conciliaciones-persistence-jpa-parent`.

## Comandos
```bash
mvn clean install -DskipTests
mvn spring-boot:run
```

## Endpoints base
- `GET /api/v1/files/health`
- `POST /api/v1/files`
- `GET /api/v1/files/{id}`
- `GET /swagger-ui.html`

## Estructura
- `application`: puertos de entrada/salida y casos de uso
- `domain`: modelos internos del micro
- `infrastructure.adapter.in.rest`: controladores y DTOs
- `infrastructure.adapter.out.persistence`: adaptador que delega en `conciliaciones-persistence-jpa`
- `infrastructure.config`: configuración técnica
