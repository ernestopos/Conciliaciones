export type CrudResourceKey =
  | 'agencies'
  | 'carriers'
  | 'clients'
  | 'producers'
  | 'parameters'
  | 'reconciliation-cases';

export type CrudFieldType = 'text' | 'textarea' | 'number' | 'date' | 'datetime-local' | 'checkbox' | 'select' | 'email';

export interface CrudFieldConfig {
  key: string;
  label: string;
  type?: CrudFieldType;
  required?: boolean;
  optionsResourceKey?: CrudResourceKey;
  optionValueKey?: string;
  optionLabelKey?: string;
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
