# Conciliaciones IaC

Proyecto de infraestructura como código para Docker y PostgreSQL.

## Levantar
docker compose -f compose/docker-compose.yml up -d

## Bajar
docker compose -f compose/docker-compose.yml down

## Microservicio de reportes

El compose `compose/docker-compose.microservices.yml` incluye `ms-reporting-service` expuesto en el puerto `9095`.

Endpoint base local:

```text
http://localhost:9095/api/v1
```

El frontend recibe la URL mediante `FRONTEND_API_REPORTING_URL`.
