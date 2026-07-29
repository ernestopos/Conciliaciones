import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { finalize } from 'rxjs/operators';
import { SecureBrowserLaunchResponse } from '../../../models/secure-browser.model';
import { SecureBrowserService } from '../../../services/secure-browser.service';
import { CarrierPortal } from '../../../models/carrier-portal.model';
import { CarrierPortalService } from '../../../services/carrier-portal.service';

@Component({
  selector: 'app-carrier-portals-dialog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatProgressSpinnerModule
  ],
  template: `
    <div class="dialog-shell">
      <header class="dialog-header">
        <div class="header-icon">
          <mat-icon>hub</mat-icon>
        </div>
        <div class="header-copy">
          <span class="eyebrow">NEXCOVERIN TRUST CONNECTION</span>
          <h2>Select an insurance carrier portal</h2>
          <p>Choose the authorized portal you want to open through the secure U.S. network.</p>
        </div>
        <button mat-icon-button type="button" aria-label="Close" (click)="dialogRef.close()">
          <mat-icon>close</mat-icon>
        </button>
      </header>

      <mat-dialog-content>
        <div class="security-note">
          <mat-icon>verified_user</mat-icon>
          <span>Your Nexcoverin session remains protected while the selected carrier portal is validated.</span>
        </div>

        <div *ngIf="loading" class="loading-state">
          <mat-spinner diameter="42"></mat-spinner>
          <h3>Loading carrier portals</h3>
          <p>Please wait while Nexcoverin retrieves the authorized portals.</p>
        </div>

        <div *ngIf="!loading && errorMessage" class="error-state">
          <mat-icon>cloud_off</mat-icon>
          <h3>Carrier portals could not be loaded</h3>
          <p>{{ errorMessage }}</p>
          <button mat-stroked-button type="button" (click)="loadPortals()">
            <mat-icon>refresh</mat-icon>
            Try again
          </button>
        </div>

        <ng-container *ngIf="!loading && !errorMessage">
          <mat-form-field appearance="outline" class="search-field">
            <mat-label>Search carrier portal</mat-label>
            <mat-icon matPrefix>search</mat-icon>
            <input matInput [(ngModel)]="searchTerm" placeholder="Carrier name or code" />
            <button *ngIf="searchTerm" mat-icon-button matSuffix type="button"
                    aria-label="Clear search" (click)="searchTerm = ''">
              <mat-icon>close</mat-icon>
            </button>
          </mat-form-field>

          <div class="portal-grid" *ngIf="filteredPortals.length; else emptyState">
            <button *ngFor="let portal of filteredPortals; trackBy: trackByPortalId"
                    type="button" class="portal-card"
                    [class.selected]="selectedPortal?.id === portal.id"
                    (click)="selectPortal(portal)">
              <div class="portal-content">
                <div class="portal-heading">
                  <div>
                    <span class="portal-code">{{ portal.code }}</span>
                    <h3>{{ portal.displayName }}</h3>
                    <span *ngIf="portal.carrierName" class="carrier-name">{{ portal.carrierName }}</span>
                  </div>
                  <mat-icon class="selection-icon">
                    {{ selectedPortal?.id === portal.id ? 'check_circle' : 'radio_button_unchecked' }}
                  </mat-icon>
                </div>

                <p>{{ portal.description || 'Authorized insurance carrier transaction portal.' }}</p>

                <div class="portal-features">
                  <span *ngIf="portal.allowUpload"><mat-icon>upload_file</mat-icon>Upload</span>
                  <span *ngIf="portal.allowDownload"><mat-icon>download</mat-icon>Download</span>
                  <span *ngIf="portal.requiresMfa"><mat-icon>phonelink_lock</mat-icon>MFA</span>
                </div>
              </div>
            </button>
          </div>

          <ng-template #emptyState>
            <div class="empty-state">
              <mat-icon>search_off</mat-icon>
              <h3>{{ portals.length ? 'No carrier portals found' : 'No carrier portals available' }}</h3>
              <p>{{ portals.length ? 'Try a different name or code.' : 'There are currently no active portals configured.' }}</p>
            </div>
          </ng-template>
        </ng-container>
      </mat-dialog-content>

      <mat-dialog-actions align="end">
        <button mat-button type="button" (click)="dialogRef.close()">Cancel</button>
        <button mat-flat-button class="continue-button" type="button"
                [disabled]="!selectedPortal || loading || launching || !!errorMessage"
                (click)="confirmSelection()">
          <mat-spinner *ngIf="launching" diameter="20"></mat-spinner>
          <mat-icon *ngIf="!launching">vpn_lock</mat-icon>
          {{ launching ? 'Opening secure browser...' : 'Continue securely' }}
        </button>
      </mat-dialog-actions>
    </div>
  `,
  styles: [`
    :host { display: block; }
    .dialog-shell { width: min(920px, 90vw); max-width: 100%; color: #14243a; }
    .dialog-header { display: grid; grid-template-columns: auto 1fr auto; gap: 16px; align-items: start; padding: 26px 26px 18px; border-bottom: 1px solid #e4ebf3; background: linear-gradient(135deg, #f7fbff 0%, #edf6ff 100%); }
    .header-icon { display: grid; width: 48px; height: 48px; place-items: center; border-radius: 15px; color: #fff; background: linear-gradient(135deg, #0b4f9c, #10a9db); box-shadow: 0 10px 22px rgba(20, 119, 197, .22); }
    .header-icon mat-icon { width: 27px; height: 27px; font-size: 27px; }
    .header-copy { min-width: 0; }
    .eyebrow { color: #1976c9; font-size: 10px; font-weight: 800; letter-spacing: .14em; }
    h2 { margin: 4px 0; font-size: 25px; line-height: 1.2; }
    .header-copy p { margin: 0; color: #607287; line-height: 1.45; }
    mat-dialog-content { padding: 22px 26px 10px !important; max-height: 65vh; }
    .security-note { display: flex; gap: 10px; align-items: center; margin-bottom: 18px; padding: 12px 14px; border: 1px solid #d4e7f7; border-radius: 12px; color: #315b7c; background: #f3f9ff; font-size: 13px; }
    .security-note mat-icon { color: #1680c9; }
    .search-field { width: 100%; }
    .portal-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 14px; padding: 4px 0 10px; }
    .portal-card { display: block; width: 100%; padding: 18px; border: 1px solid #dce5ef; border-radius: 16px; text-align: left; color: inherit; background: #fff; cursor: pointer; transition: border-color .18s ease, box-shadow .18s ease, transform .18s ease; }
    .portal-card:hover { border-color: #8fc8ee; box-shadow: 0 10px 25px rgba(26, 89, 142, .10); transform: translateY(-2px); }
    .portal-card.selected { border-color: #1688d2; background: linear-gradient(135deg, #f8fcff, #eef8ff); box-shadow: 0 0 0 2px rgba(22, 136, 210, .12); }
    .portal-content { min-width: 0; }
    .portal-heading { display: flex; justify-content: space-between; gap: 12px; }
    .portal-code { color: #3d82be; font-size: 9px; font-weight: 800; letter-spacing: .12em; }
    h3 { margin: 2px 0 0; font-size: 17px; }
    .carrier-name { display: block; margin-top: 3px; color: #8391a1; font-size: 11px; }
    .selection-icon { flex: 0 0 auto; color: #1989d2; }
    .portal-content p { margin: 10px 0 13px; color: #66778a; font-size: 13px; line-height: 1.45; }
    .portal-features { display: flex; flex-wrap: wrap; gap: 8px; }
    .portal-features span { display: inline-flex; align-items: center; gap: 4px; padding: 5px 8px; border-radius: 999px; color: #49647d; background: #edf3f8; font-size: 10px; font-weight: 700; }
    .portal-features mat-icon { width: 14px; height: 14px; font-size: 14px; }
    .loading-state, .error-state, .empty-state { display: flex; min-height: 230px; flex-direction: column; align-items: center; justify-content: center; padding: 30px; text-align: center; color: #607287; }
    .loading-state h3, .error-state h3, .empty-state h3 { margin: 14px 0 4px; }
    .loading-state p, .error-state p, .empty-state p { margin: 0 0 14px; }
    .error-state > mat-icon, .empty-state > mat-icon { width: 46px; height: 46px; font-size: 46px; color: #8ea5b9; }
    .error-state > mat-icon { color: #c75b55; }
    mat-dialog-actions { padding: 14px 26px 22px !important; border-top: 1px solid #edf1f5; }
    .continue-button { min-height: 42px; border-radius: 10px; color: #fff !important; background: linear-gradient(90deg, #0868bd, #16a7d6) !important; }
    .continue-button:disabled { opacity: .45; }
    @media (max-width: 700px) {
      .dialog-shell { width: 94vw; }
      .dialog-header { padding: 20px; }
      mat-dialog-content { padding: 18px 20px 8px !important; }
      .portal-grid { grid-template-columns: 1fr; }
      mat-dialog-actions { padding: 12px 20px 18px !important; }
    }
  `]
})
export class CarrierPortalsDialogComponent implements OnInit {
  searchTerm = '';
  selectedPortal?: CarrierPortal;
  portals: CarrierPortal[] = [];
  loading = false;
  launching = false;
  errorMessage = '';

  constructor(
    public readonly dialogRef: MatDialogRef<CarrierPortalsDialogComponent>,
    private readonly carrierPortalService: CarrierPortalService,
    private readonly secureBrowserService: SecureBrowserService
  ) {}

  ngOnInit(): void {
    this.loadPortals();
  }

  get filteredPortals(): CarrierPortal[] {
    const value = this.searchTerm.trim().toLowerCase();
    if (!value) return this.portals;

    return this.portals.filter((portal) =>
      portal.displayName.toLowerCase().includes(value) ||
      portal.code.toLowerCase().includes(value) ||
      (portal.carrierName?.toLowerCase().includes(value) ?? false) ||
      (portal.carrierCode?.toLowerCase().includes(value) ?? false)
    );
  }

  loadPortals(): void {
    this.loading = true;
    this.errorMessage = '';
    this.selectedPortal = undefined;

    this.carrierPortalService.listActive()
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (portals) => this.portals = portals,
        error: (error) => {
          console.error('Error loading carrier portals', error);
          this.portals = [];
          this.errorMessage = 'Verify the connection with ms-core and try again.';
        }
      });
  }

  selectPortal(portal: CarrierPortal): void {
    this.selectedPortal = portal;
  }

  confirmSelection(): void {
    if (!this.selectedPortal || this.launching) {
      return;
    }

    const selectedPortal = this.selectedPortal;
    const secureWindow = window.open('about:blank', '_blank');

    if (!secureWindow) {
      this.errorMessage = 'Your browser blocked the secure window. Allow pop-ups for Nexcoverin and try again.';
      return;
    }

    // Prevent the opened page from controlling the Nexcoverin window.
    try {
      secureWindow.opener = null;
      secureWindow.document.title = 'Nexcoverin Secure Connection';
      secureWindow.document.body.innerHTML = `
        <div style="font-family:Arial,sans-serif;display:grid;place-items:center;min-height:90vh;color:#17324d">
          <div style="text-align:center">
            <h2>Establishing secure connection...</h2>
            <p>Please wait while Nexcoverin validates access to ${this.escapeHtml(selectedPortal.displayName)}.</p>
          </div>
        </div>`;
    } catch {
      // The blank window may already be isolated by the browser; navigation can still continue.
    }

    this.launching = true;
    this.errorMessage = '';

    this.secureBrowserService.launchCarrierPortal(selectedPortal.id)
      .pipe(finalize(() => this.launching = false))
      .subscribe({
        next: (response: SecureBrowserLaunchResponse) => {
          if (!response.launchUrl) {
            secureWindow.close();
            this.errorMessage = 'The secure launch URL was not returned by ms-security.';
            return;
          }

          secureWindow.location.replace(response.launchUrl);
          this.dialogRef.close({ portal: selectedPortal, launch: response });
        },
        error: (error) => {
          console.error('Error launching WorkSpaces Secure Browser', error);
          secureWindow.close();
          this.errorMessage = this.resolveLaunchError(error);
        }
      });
  }

  private resolveLaunchError(error: any): string {
    if (error?.status === 401) {
      return 'Your Nexcoverin session is no longer valid. Sign in again and retry.';
    }
    if (error?.status === 403) {
      return 'Your user is not authorized to use Trust Connection.';
    }
    if (error?.status === 404) {
      return 'The selected carrier portal is not active or no longer exists.';
    }
    return error?.error?.message || 'The secure browser could not be started. Please try again.';
  }

  private escapeHtml(value: string): string {
    return value
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }

  trackByPortalId(_: number, portal: CarrierPortal): number {
    return portal.id;
  }
}
