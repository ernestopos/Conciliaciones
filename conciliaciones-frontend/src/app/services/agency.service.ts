import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseCrudHttpService } from '../core/services/base-crud-http.service';
import { Agency } from '../models/agency.model';

@Injectable({ providedIn: 'root' })
export class AgencyService extends BaseCrudHttpService<Agency> {
  constructor(http: HttpClient) {
    super(http, '/agencies');
  }
}
