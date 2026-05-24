import { Injectable } from '@angular/core';
import { NavItem } from '../models/navigation.models';

@Injectable({ providedIn: 'root' })
export class NavigationService {
  readonly menu: NavItem[] = [
    { label: 'Dashboard', icon: 'dashboard', route: '/dashboard' },
    {
      label: 'Seguridad',
      icon: 'security',
      children: [{ label: 'Usuarios y roles', icon: 'admin_panel_settings', route: '/seguridad/usuarios' }]
    },
    {
      label: 'Catálogos / Maestros',
      icon: 'folder_open',
      children: [
        { label: 'Clientes', icon: 'business', route: '/maestros/clientes' },
        { label: 'Productores', icon: 'groups', route: '/maestros/productores' },
        { label: 'Agencias', icon: 'apartment', route: '/maestros/agencias' },
        { label: 'Carriers', icon: 'domain', route: '/maestros/carriers' },
        { label: 'Pólizas', icon: 'assignment', route: '/maestros/polizas' },
        { label: 'Parámetros', icon: 'tune', route: '/maestros/parametros' },
        { label: 'Estados de póliza', icon: 'flag', route: '/maestros/estados-poliza' },
        { label: 'Reglas de comisión', icon: 'rule', route: '/maestros/reglas-comision' }
      ]
    },
    {
      label: 'Conciliación',
      icon: 'account_balance',
      children: [
        { label: 'Archivos fuente', icon: 'upload_file', route: '/conciliacion/source-files' },
        { label: 'Ejecuciones', icon: 'sync', route: '/conciliacion/processing-executions' },
        { label: 'Casos de conciliación', icon: 'fact_check', route: '/conciliacion/casos' }
      ]
    },
    {
      label: 'Upload Files',
      icon: 'upload_file',
      children: [
        {
          label: 'Upload sources file',
          icon: 'description',
          route: '/upload-files/upload-sources-file'
        }
      ]
    },
    {
      label: 'Pagos / Liquidaciones',
      icon: 'payments',
      route: '/pagos/liquidaciones'
    },
    { label: 'Auditoría', icon: 'history', route: '/auditoria/registros' },
    { label: 'Reportes', icon: 'assessment', route: '/reportes' }
  ];
}
