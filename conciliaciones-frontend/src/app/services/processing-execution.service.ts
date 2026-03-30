import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { ProcessingExecutionModel } from '../models/processing-execution.model';

@Injectable({ providedIn: 'root' })
export class ProcessingExecutionService {
  list(): Observable<ProcessingExecutionModel[]> {
    return of([
      { id: 10, sourceFileId: 1, executionId: 'PROC-20260330-001', status: 'COMPLETED', detailMessage: 'Procesamiento finalizado con éxito.' },
      { id: 11, sourceFileId: 2, executionId: 'PROC-20260330-002', status: 'IN_PROGRESS', detailMessage: 'Validando registros crudos.' }
    ]).pipe(delay(350));
  }
}
