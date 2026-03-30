import { Routes } from '@angular/router';
import { authGuard } from './auth/guards/auth.guard';
import { AppLayoutComponent } from './shared/components/app-layout/app-layout.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { LoginPageComponent } from './features/auth/pages/login-page/login-page.component';
import { SourceFilesPageComponent } from './features/conciliacion/source-files/source-files-page.component';
import { ProcessingExecutionsPageComponent } from './features/conciliacion/processing-executions/processing-executions-page.component';
import { DomainPlaceholderComponent } from './features/placeholders/domain-placeholder.component';

export const routes: Routes = [
  { path: 'auth/login', component: LoginPageComponent },
  {
    path: 'app',
    component: AppLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'conciliacion/source-files', component: SourceFilesPageComponent },
      { path: 'conciliacion/processing-executions', component: ProcessingExecutionsPageComponent },
      { path: 'seguridad/usuarios', component: DomainPlaceholderComponent, data: { title: 'Seguridad' } },
      { path: 'maestros/clientes', component: DomainPlaceholderComponent, data: { title: 'Clientes' } },
      { path: 'maestros/productores', component: DomainPlaceholderComponent, data: { title: 'Productores' } },
      { path: 'maestros/agencias', component: DomainPlaceholderComponent, data: { title: 'Agencias' } },
      { path: 'maestros/estados-poliza', component: DomainPlaceholderComponent, data: { title: 'Estados de Póliza' } },
      { path: 'maestros/reglas-comision', component: DomainPlaceholderComponent, data: { title: 'Reglas de Comisión' } },
      { path: 'conciliacion/casos', component: DomainPlaceholderComponent, data: { title: 'Casos de Conciliación' } },
      { path: 'pagos/liquidaciones', component: DomainPlaceholderComponent, data: { title: 'Pagos / Liquidaciones' } },
      { path: 'auditoria/registros', component: DomainPlaceholderComponent, data: { title: 'Auditoría' } },
      { path: 'reportes', component: DomainPlaceholderComponent, data: { title: 'Reportes' } },
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' }
    ]
  },
  { path: '', pathMatch: 'full', redirectTo: 'auth/login' },
  { path: '**', redirectTo: '/auth/login' }
];
