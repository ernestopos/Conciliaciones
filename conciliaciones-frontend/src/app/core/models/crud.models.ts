export type CrudResourceKey =
  | 'agencies'
  | 'carriers'
  | 'clients'
  | 'producers'
  | 'parameters'
  | 'policies'
  | 'reconciliation-cases';

export type CrudFieldType = 'text' | 'textarea' | 'number' | 'date' | 'datetime-local' | 'checkbox' | 'lookup';

export interface CrudLookupConfig {
  resourceKey: CrudResourceKey;
  valueKey: string;
  displayKey: string;
  title: string;
  columns: string[];
  headers: Record<string, string>;
}

export interface CrudFieldConfig {
  key: string;
  label: string;
  type?: CrudFieldType;
  required?: boolean;
  displayKey?: string;
  lookup?: CrudLookupConfig;
}

export interface CrudRouteConfig {
  resourceKey: CrudResourceKey;
  title: string;
  subtitle: string;
  createLabel: string;
  columns: string[];
  headers: Record<string, string>;
  fields: CrudFieldConfig[];
  allowReprocess?: boolean;
}
