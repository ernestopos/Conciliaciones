# ms-notifications

Cascarón base del microservicio de notificaciones.

## Stack
- Java 21
- Spring Boot 3.3.8
- Arquitectura hexagonal
- Configuración `.properties`
- Integración con `conciliaciones-domain-model`
- Integración con `conciliaciones-persistence-jpa`

## Responsabilidad
Gestionar el envío de notificaciones por correo y dejar lista la base para otros canales como SMS o push.

## Build
```bash
mvn clean install -DskipTests
```

## Docker
```bash
docker build -t ms-notifications .
```
