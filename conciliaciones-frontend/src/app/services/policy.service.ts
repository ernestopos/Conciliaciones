import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseCrudHttpService } from '../core/services/base-crud-http.service';
import { Policy } from '../models/policy.model';

@Injectable({ providedIn: 'root' })
export class PolicyService extends BaseCrudHttpService<Policy> {
  constructor(http: HttpClient) {
    super(http, '/policies');
  }
}
