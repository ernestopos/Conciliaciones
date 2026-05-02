import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseCrudHttpService } from '../core/services/base-crud-http.service';
import { Carrier } from '../models/carrier.model';

@Injectable({ providedIn: 'root' })
export class CarrierService extends BaseCrudHttpService<Carrier> {
  constructor(http: HttpClient) {
    super(http, '/carriers');
  }
}
