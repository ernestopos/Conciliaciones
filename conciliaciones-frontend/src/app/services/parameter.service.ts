import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseCrudHttpService } from '../core/services/base-crud-http.service';
import { Parameter } from '../models/parameter.model';

@Injectable({ providedIn: 'root' })
export class ParameterService extends BaseCrudHttpService<Parameter> {
  constructor(http: HttpClient) {
    super(http, '/parameters');
  }
}
