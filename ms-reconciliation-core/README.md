# ms-reconciliation-core

Cascarón base del microservicio de conciliación.

## Stack
- Java 21
- Spring Boot 3.3.8
- Arquitectura hexagonal
- Configuración `.properties`
- Integración con `conciliaciones-domain-model`
- Integración con `conciliaciones-persistence-jpa`

## Estructura
- `application`: puertos y casos de uso
- `domain`: modelos y enums del micro
- `infrastructure`: adapters, config, controllers y persistencia

## Build
```bash
mvn clean install -DskipTests
```

## Docker
```bash
docker build -t ms-reconciliation-core .
```
