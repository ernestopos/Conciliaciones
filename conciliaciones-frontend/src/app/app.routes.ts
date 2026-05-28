import { Routes } from '@angular/router';
import { authGuard } from './auth/guards/auth.guard';
import { AppLayoutComponent } from './shared/components/app-layout/app-layout.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { LoginPageComponent } from './features/auth/pages/login-page/login-page.component';
import { SourceFilesPageComponent } from './features/conciliacion/source-files/source-files-page.component';
import { ProcessingExecutionsPageComponent } from './features/conciliacion/processing-executions/processing-executions-page.component';
import { ProcessingExecutionDetailPageComponent } from './features/conciliacion/processing-execution-detail/processing-execution-detail-page.component';
import { DomainPlaceholderComponent } from './features/placeholders/domain-placeholder.component';
import { CrudEntityPageComponent } from './shared/components/crud-entity-page/crud-entity-page.component';
import { CrudRouteConfig } from './core/models/crud.models';
import { UploadSourcesFilePageComponent } from './features/upload-files/upload-sources-file/upload-sources-file-page.component';

const agencyCrudConfig: CrudRouteConfig = {
  resourceKey: 'agencies',
  title: 'Agencias',
  subtitle: 'Administración de agencias del módulo de conciliaciones.',
  createLabel: 'Nueva agencia',
  columns: ['id', 'carrierId', 'externalAgencyId', 'name', 'active'],
  headers: { id: 'ID', carrierId: 'Carrier', externalAgencyId: 'ID externo', name: 'Nombre', active: 'Activo' },
  fields: [
    {
      key: 'carrierId',
      label: 'Carrier',
      type: 'select',
      required: true,
      optionsResourceKey: 'carriers',
      optionValueKey: 'id',
      optionLabelKey: 'name'
    },
    { key: 'externalAgencyId', label: 'ID externo' },
    { key: 'name', label: 'Nombre', required: true },
    { key: 'active', label: 'Activo', type: 'checkbox' }
  ]
};

const carrierCrudConfig: CrudRouteConfig = {
  resourceKey: 'carriers',
  title: 'Carriers',
  subtitle: 'Administración de aseguradoras / carriers.',
  createLabel: 'Nuevo carrier',
  columns: ['id', 'code', 'name', 'description', 'active'],
  headers: { id: 'ID', code: 'Código', name: 'Nombre', description: 'Descripción', active: 'Activo' },
  fields: [
    { key: 'code', label: 'Código', required: true },
    { key: 'name', label: 'Nombre', required: true },
    { key: 'description', label: 'Descripción', type: 'textarea' },
    { key: 'active', label: 'Activo', type: 'checkbox' }
  ]
};

const clientCrudConfig: CrudRouteConfig = {
  resourceKey: 'clients',
  title: 'Clientes',
  subtitle: 'Catálogo maestro de clientes.',
  createLabel: 'Nuevo cliente',
  columns: ['id', 'externalClientId', 'fullName', 'state', 'active'],
  headers: { id: 'ID', externalClientId: 'ID externo', fullName: 'Nombre completo', state: 'Estado', active: 'Activo' },
  fields: [
    { key: 'externalClientId', label: 'ID externo' },
    { key: 'firstName', label: 'Primer nombre' },
    { key: 'middleName', label: 'Segundo nombre' },
    { key: 'lastName', label: 'Apellidos' },
    { key: 'fullName', label: 'Nombre completo', required: true },
    { key: 'birthDate', label: 'Fecha de nacimiento', type: 'date' },
    { key: 'state', label: 'Estado' },
    { key: 'active', label: 'Activo', type: 'checkbox' }
  ]
};

const producerCrudConfig: CrudRouteConfig = {
  resourceKey: 'producers',
  title: 'Productores',
  subtitle: 'Catálogo maestro de productores.',
  createLabel: 'Nuevo productor',
  columns: ['id', 'agencyId', 'externalProducerId', 'fullName', 'email', 'active'],
  headers: { id: 'ID', agencyId: 'Agencia', externalProducerId: 'ID externo', fullName: 'Nombre completo', email: 'Correo', active: 'Activo' },
  fields: [
    { key: 'agencyId', label: 'Agencia', type: 'select', required: true, optionsResourceKey: 'agencies', optionValueKey: 'id', optionLabelKey: 'name' },
    { key: 'externalProducerId', label: 'ID externo' },
    { key: 'firstName', label: 'Nombres' },
    { key: 'lastName', label: 'Apellidos' },
    { key: 'fullName', label: 'Nombre completo', required: true },
    { key: 'email', label: 'Correo', type: 'email' },
    { key: 'phone', label: 'Teléfono' },
    { key: 'npn', label: 'NPN' },
    { key: 'taxIdMasked', label: 'Tax ID mascarado' },
    { key: 'active', label: 'Activo', type: 'checkbox' }
  ]
};

const parameterCrudConfig: CrudRouteConfig = {
  resourceKey: 'parameters',
  title: 'Parámetros',
  subtitle: 'Parámetros configurables de conciliaciones.',
  createLabel: 'Nuevo parámetro',
  columns: ['id', 'name', 'parameterGroup', 'value', 'sortOrder', 'active'],
  headers: { id: 'ID', name: 'Nombre', parameterGroup: 'Grupo', value: 'Valor', sortOrder: 'Orden', active: 'Activo' },
  fields: [
    { key: 'name', label: 'Nombre', required: true },
    { key: 'description', label: 'Descripción', type: 'textarea' },
    { key: 'value', label: 'Valor', required: true },
    { key: 'parameterGroup', label: 'Grupo', required: true },
    { key: 'sortOrder', label: 'Orden', type: 'number', required: true },
    { key: 'active', label: 'Activo', type: 'checkbox' }
  ]
};

const reconciliationCaseCrudConfig: CrudRouteConfig = {
  resourceKey: 'reconciliation-cases',
  title: 'Casos de conciliación',
  subtitle: 'Gestión de discrepancias detectadas durante la conciliación.',
  createLabel: 'Nuevo caso',
  allowReprocess: true,
  columns: ['id', 'sourceFileId', 'carrierId', 'caseTypeId', 'severityId', 'statusId'],
  headers: { id: 'ID', sourceFileId: 'Archivo fuente', carrierId: 'Carrier', caseTypeId: 'Tipo caso', severityId: 'Severidad', statusId: 'Estado' },
  fields: [
    { key: 'sourceFileId', label: 'Archivo fuente ID', type: 'number', required: true },
    { key: 'commissionStatementId', label: 'Liquidación ID', type: 'number' },
    { key: 'commissionStatementItemId', label: 'Ítem liquidación ID', type: 'number' },
    { key: 'carrierId', label: 'Carrier ID', type: 'number', required: true },
    { key: 'policyId', label: 'Póliza ID', type: 'number' },
    { key: 'producerId', label: 'Productor ID', type: 'number' },
    { key: 'caseTypeId', label: 'Tipo caso ID', type: 'number', required: true },
    { key: 'severityId', label: 'Severidad ID', type: 'number', required: true },
    { key: 'statusId', label: 'Estado ID', type: 'number', required: true },
    { key: 'detectedAt', label: 'Fecha detección', type: 'datetime-local', required: true },
    { key: 'description', label: 'Descripción', type: 'textarea', required: true },
    { key: 'suggestedAction', label: 'Acción sugerida', type: 'textarea' },
    { key: 'resolutionNotes', label: 'Notas resolución', type: 'textarea' },
    { key: 'resolvedAt', label: 'Fecha resolución', type: 'datetime-local' },
    { key: 'resolvedBy', label: 'Resuelto por' }
  ]
};

export const routes: Routes = [
  { path: 'auth/login', component: LoginPageComponent },
  {
    path: '',
    component: AppLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'conciliacion/source-files', component: SourceFilesPageComponent },
      { path: 'conciliacion/processing-executions', component: ProcessingExecutionsPageComponent },
      { path: 'conciliacion/processing-executions/:id', component: ProcessingExecutionDetailPageComponent },
      { path: 'seguridad/usuarios', component: DomainPlaceholderComponent, data: { title: 'Seguridad' } },
      { path: 'maestros/agencias', component: CrudEntityPageComponent, data: { crudConfig: agencyCrudConfig } },
      { path: 'maestros/carriers', component: CrudEntityPageComponent, data: { crudConfig: carrierCrudConfig } },
      { path: 'maestros/clientes', component: CrudEntityPageComponent, data: { crudConfig: clientCrudConfig } },
      { path: 'maestros/productores', component: CrudEntityPageComponent, data: { crudConfig: producerCrudConfig } },
      { path: 'maestros/parametros', component: CrudEntityPageComponent, data: { crudConfig: parameterCrudConfig } },
      { path: 'maestros/estados-poliza', component: DomainPlaceholderComponent, data: { title: 'Estados de Póliza' } },
      { path: 'maestros/reglas-comision', component: DomainPlaceholderComponent, data: { title: 'Reglas de Comisión' } },
      { path: 'conciliacion/casos', component: CrudEntityPageComponent, data: { crudConfig: reconciliationCaseCrudConfig } },
      { path: 'pagos/liquidaciones', component: DomainPlaceholderComponent, data: { title: 'Pagos / Liquidaciones' } },
      { path: 'auditoria/registros', component: DomainPlaceholderComponent, data: { title: 'Auditoría' } },
      { path: 'reportes', component: DomainPlaceholderComponent, data: { title: 'Reportes' } },
      { path: 'upload-files/upload-sources-file',  component: UploadSourcesFilePageComponent },
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' }
    ]
  },
  { path: '**', redirectTo: '/dashboard' }
];
