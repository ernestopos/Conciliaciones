import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseCrudHttpService } from '../core/services/base-crud-http.service';
import { SecurityRole } from '../models/security-role.model';

@Injectable({ providedIn: 'root' })
export class SecurityRoleService extends BaseCrudHttpService<SecurityRole> {
  constructor(http: HttpClient) {
    super(http, '/security/roles');
  }
}
