import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseCrudHttpService } from '../core/services/base-crud-http.service';
import { Producer } from '../models/producer.model';

@Injectable({ providedIn: 'root' })
export class ProducerService extends BaseCrudHttpService<Producer> {
  constructor(http: HttpClient) {
    super(http, '/producers');
  }
}
