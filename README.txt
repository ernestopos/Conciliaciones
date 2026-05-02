Archivos generados para ms-reconciliation-core sin modificar codigo fuente del micro.

Copiar en cada proyecto:

1) ms-reconciliation-core/Dockerfile
2) ms-reconciliation-core/.dockerignore
3) conciliaciones-iac/compose/docker-compose.microservices.yml
4) conciliaciones-iac/compose/docker-compose.orchestrator.yml

Comando desde conciliaciones-iac para levantar solo ms-reconciliation-core:

docker compose --env-file .env -f compose/docker-compose.orchestrator.yml up --build -d ms-reconciliation-core

Logs:

docker compose --env-file .env -f compose/docker-compose.orchestrator.yml logs -f ms-reconciliation-core

Parar solo el micro:

docker compose --env-file .env -f compose/docker-compose.orchestrator.yml stop ms-reconciliation-core

Bajar todo el stack:

docker compose --env-file .env -f compose/docker-compose.orchestrator.yml down

Nota:
- El Dockerfile copia el jar desde target/ms-reconciliation-core-*.jar.
- Primero compila el micro en local: mvn clean package -DskipTests
- El compose apunta DB a postgres:5432, Keycloak a keycloak:8080 y LocalStack a localstack:4566.
