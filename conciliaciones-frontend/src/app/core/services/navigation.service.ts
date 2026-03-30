import { Injectable } from '@angular/core';
import { NavItem } from '../models/navigation.models';

@Injectable({ providedIn: 'root' })
export class NavigationService {
  readonly menu: NavItem[] = [
    { label: 'Dashboard', icon: 'dashboard', route: '/app/dashboard' },
    {
      label: 'Seguridad',
      icon: 'security',
      children: [{ label: 'Usuarios y roles', icon: 'admin_panel_settings', route: '/app/seguridad/usuarios' }]
    },
    {
      label: 'Catálogos / Maestros',
      icon: 'folder_open',
      children: [
        { label: 'Clientes', icon: 'business', route: '/app/maestros/clientes' },
        { label: 'Productores', icon: 'groups', route: '/app/maestros/productores' },
        { label: 'Agencias', icon: 'apartment', route: '/app/maestros/agencias' },
        { label: 'Estados de póliza', icon: 'flag', route: '/app/maestros/estados-poliza' },
        { label: 'Reglas de comisión', icon: 'rule', route: '/app/maestros/reglas-comision' }
      ]
    },
    {
      label: 'Conciliación',
      icon: 'account_balance',
      children: [
        { label: 'Archivos fuente', icon: 'upload_file', route: '/app/conciliacion/source-files' },
        { label: 'Ejecuciones', icon: 'sync', route: '/app/conciliacion/processing-executions' },
        { label: 'Casos de conciliación', icon: 'fact_check', route: '/app/conciliacion/casos' }
      ]
    },
    {
      label: 'Pagos / Liquidaciones',
      icon: 'payments',
      route: '/app/pagos/liquidaciones'
    },
    { label: 'Auditoría', icon: 'history', route: '/app/auditoria/registros' },
    { label: 'Reportes', icon: 'assessment', route: '/app/reportes' }
  ];
}
