import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseCrudHttpService } from '../core/services/base-crud-http.service';
import { environment } from '../../environments/environment';
import { Parameter } from '../models/parameter.model';

@Injectable({ providedIn: 'root' })
export class ParameterService extends BaseCrudHttpService<Parameter> {
  constructor(http: HttpClient) {
    super(http, '/parameters');
  }

  findByGroup(parameterGroup: string): Observable<Parameter[]> {
    return this.http.get<Parameter[]>(
      `${environment.api.core}/parameters/by-group/${encodeURIComponent(parameterGroup)}`
    );
  }
}
