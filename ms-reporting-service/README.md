# ms-reporting-service

Microservicio de reportes para Conciliaciones usando JasperReports.

## Seguridad

Replica el esquema de `ms-reconciliation-core`:

- Spring Security
- OAuth2 Resource Server
- JWT issuer configurado por `JWT_ISSUER_URI`
- Swagger y health públicos
- Resto de endpoints autenticados

## Endpoints

- `GET /actuator/health` público
- `GET /swagger-ui.html` público
- `GET /api/v1/reports/ping` protegido
- `POST /api/v1/reports/generate` protegido

## Ejemplo request

```json
{
  "reportCode": "sample-report",
  "format": "PDF",
  "fromDate": "2026-01-01",
  "toDate": "2026-12-31",
  "parameters": {}
}
```

## Reporte implementado: PaymentForEachProducer

Genera el reporte de comisiones por productor usando la consulta JPQL ubicada en `CommissionPaymentRepository.findCommissionReport(Pageable pageable)` del proyecto JPA compartido.

Endpoint:

```http
POST /api/v1/reports/generate
Authorization: Bearer <token>
Content-Type: application/json
```

Body PDF:

```json
{
  "reportCode": "PaymentForEachProducer",
  "format": "PDF",
  "parameters": {
    "limit": 100
  }
}
```

Body Excel:

```json
{
  "reportCode": "PaymentForEachProducer",
  "format": "XLSX",
  "parameters": {
    "limit": 100
  }
}
```

El parámetro `limit` es opcional. Por defecto trae 100 registros y se limita máximo a 500.
