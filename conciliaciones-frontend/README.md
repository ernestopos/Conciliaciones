# Conciliaciones Frontend (Base Demo Angular)

Frontend base orientado a demo corporativa con Angular + Angular Material + Reactive Forms.

## Estructura principal

- `auth/`: contrato, servicio y provider dummy preparado para reemplazar por Keycloak.
- `core/`: modelos y servicios transversales (navegación).
- `shared/components/`: componentes reutilizables de layout/UI.
- `features/`: páginas de negocio y dominios.
- `services/` + `models/`: servicios HTTP mock y modelos tipados.

## Pantallas creadas

1. `auth/login` (login responsive 2 columnas, captcha, mostrar/ocultar contraseña).
2. `app/dashboard` (KPIs y auditoría reciente mock).
3. `app/conciliacion/source-files` (tabla base + filtros + paginación/sort).
4. `app/conciliacion/processing-executions` (tabla base + filtros + paginación/sort).
5. Placeholders navegables para seguridad, maestros, pagos, auditoría y reportes.

## Componentes reutilizables creados

- `app-layout`
- `login-form`
- `captcha`
- `page-header`
- `breadcrumb`
- `confirm-dialog`
- `generic-table`
- `status-chip`
- `loading-spinner`
- `empty-state`
- `search-filter-bar`
- `side-menu`
- `top-toolbar`

## Entidades reales detectadas en `conciliaciones-persistence-jpa-parent`

- `source_file` (`SourceFileEntity`)
- `processing_execution` (`ProcessingExecutionEntity`)

Se generaron modelos y vistas base alineadas a estas entidades.

## Seguridad demo (mock)

- `AuthProviderPort` define el contrato desacoplado.
- `DummyAuthProvider` implementa login temporal con storage local para demo.
- `AuthService` expone `login/logout/isAuthenticated/getCurrentUser`.
- `authGuard` protege rutas privadas.
- `authInterceptor` queda preparado para token real.

### Credenciales demo

- usuario: `admin@conciliaciones.com`
- contraseña: `Admin123*`

## Qué quedó mockeado vs real

### Mockeado
- Login/captcha temporal.
- Dashboard KPIs.
- Listados de entidades en memoria (`SourceFileService`, `ProcessingExecutionService`).

### Real / preparado
- Arquitectura modular, reusable y desacoplada.
- Routing de dominios y layout administrativo.
- Contrato para reemplazo de autenticación con Keycloak.

## Pasos para integrar backend real

1. Sustituir `DummyAuthProvider` por `KeycloakAuthProvider` manteniendo `AuthProviderPort`.
2. Inyectar token en `authInterceptor`.
3. Reemplazar servicios mock por `HttpClient` contra APIs reales.
4. Añadir mapeadores DTO -> modelo UI según contratos backend.

## Troubleshooting rápido (`ng serve` en blanco)

Si la app muestra pantalla en blanco, verificar:

1. Dependencias instaladas (`npm install`).
2. Polyfills activos (`angular.json` declara `zone.js`) y `main.ts` también lo importa explícitamente.
3. Reiniciar el dev server después de cambios de configuración:
   - `Ctrl + C`
   - `npm run start`

### Notas sobre `npm install` en Windows

- Mensajes `npm warn deprecated ...` no bloquean la ejecución (son advertencias de dependencias transitivas).
- Mensajes `npm warn cleanup EPERM ...` indican archivos bloqueados por el sistema (normalmente antivirus/editor/terminal).
- Si pasa, usar:
  1. Cerrar VS Code y terminales sobre la carpeta.
  2. Ejecutar terminal como Administrador.
  3. `npm run reinstall`
  4. `npm run start`
