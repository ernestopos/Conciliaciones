import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseCrudHttpService } from '../core/services/base-crud-http.service';
import { Client } from '../models/client.model';

@Injectable({ providedIn: 'root' })
export class ClientService extends BaseCrudHttpService<Client> {
  constructor(http: HttpClient) {
    super(http, '/clients');
  }
}
