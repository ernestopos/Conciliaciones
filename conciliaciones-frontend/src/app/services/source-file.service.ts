import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { SourceFileModel } from '../models/source-file.model';

@Injectable({ providedIn: 'root' })
export class SourceFileService {
  list(): Observable<SourceFileModel[]> {
    return of([
      { id: 1, clientId: 101, originalFileName: 'cartera_marzo.xlsx', mimeType: 'application/vnd.ms-excel', fileSize: 120030, fileType: 'XLSX', checksum: 'abc123', processingStatus: 'COMPLETED' },
      { id: 2, clientId: 204, originalFileName: 'pagos_lote.csv', mimeType: 'text/csv', fileSize: 20110, fileType: 'CSV', checksum: 'def456', processingStatus: 'PENDING' }
    ]).pipe(delay(350));
  }
}
