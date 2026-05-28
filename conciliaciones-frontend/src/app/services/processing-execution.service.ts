import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { normalizeCollectionResponse } from '../core/services/api-response.utils';
import { ProcessingExecutionDetailModel, ProcessingExecutionModel, ValidationExecutionDetailModel } from '../models/processing-execution.model';

@Injectable({ providedIn: 'root' })
export class ProcessingExecutionService {
  private readonly resourceUrl = `${environment.api.core}/execution-plan-tasks`;

  constructor(private readonly http: HttpClient) {}

  list(): Observable<ProcessingExecutionModel[]> {
    return this.http
      .get<unknown>(this.resourceUrl)
      .pipe(map((response) => normalizeCollectionResponse<ProcessingExecutionModel>(response)));
  }

  getDetail(id: number): Observable<ProcessingExecutionDetailModel> {
    return this.http.get<ProcessingExecutionDetailModel>(`${this.resourceUrl}/${id}`);
  }

  getValidationDetails(executionPlanTaskId: number): Observable<ValidationExecutionDetailModel[]> {
    return this.http.get<ValidationExecutionDetailModel[]>(`${this.resourceUrl}/${executionPlanTaskId}/validation-details`);
  }
}
