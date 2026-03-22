# ms-orchestrator

Cascarón base del microservicio orquestador.

## Stack
- Java 21
- Spring Boot 3.3.8
- Arquitectura hexagonal
- Configuración `.properties`
- Integración con `conciliaciones-domain-model`
- Integración con `conciliaciones-persistence-jpa`

## Responsabilidad
Centralizar la coordinación de flujos entre microservicios, disparar procesos y consultar estado de ejecución.

## Build
```bash
mvn clean install -DskipTests
```

## Docker
```bash
docker build -t ms-orchestrator .
```
