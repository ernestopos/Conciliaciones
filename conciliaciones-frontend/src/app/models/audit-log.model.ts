export interface AuditLog {
  id?: number;
  entityName: string;
  entityId: string;
  actionId: number;
  actionName?: string | null;
  username?: string | null;
  eventTimestamp: string;
  oldValues?: unknown;
  newValues?: unknown;
  details?: string | null;
}
