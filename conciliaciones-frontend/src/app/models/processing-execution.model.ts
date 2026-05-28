export interface ProcessingExecutionModel {
  id: number;
  sourceFileId: number;
  originalFileName?: string;
  statusId?: number;
  statusName: string;
  planExecuteCode: string;
  startedAt: string;
  finishedAt?: string;
  successful?: boolean;
  message?: string;
}

export interface ScheduledTaskStepModel {
  id: number;
  taskTypeId?: number;
  taskTypeName?: string;
  statusId?: number;
  statusName?: string;
  taskOrder: number;
  cronExpression?: string;
  taskBeanName?: string;
  methodName?: string;
  payload?: string;
  active?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface ProcessingExecutionDetailModel extends ProcessingExecutionModel {
  tasks: ScheduledTaskStepModel[];
}


export interface ValidationExecutionDetailModel {
  startedAt?: string;
  finishedAt?: string;
  successful?: boolean;
  message?: string;
  validationTypeDescription?: string;
  validationStatusDescription?: string;
}
