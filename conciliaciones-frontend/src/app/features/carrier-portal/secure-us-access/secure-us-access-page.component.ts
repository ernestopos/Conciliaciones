import { CommonModule } from '@angular/common';
import { Component, OnDestroy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { finalize, switchMap } from 'rxjs/operators';
import { CarrierPortal } from '../../../models/carrier-portal.model';
import { SecureBrowserLaunchResponse } from '../../../models/secure-browser.model';
import { CarrierPortalService } from '../../../services/carrier-portal.service';
import { SecureBrowserService } from '../../../services/secure-browser.service';

@Component({
  selector: 'app-secure-us-access-page',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatButtonModule,
    MatCardModule,
    MatCheckboxModule,
    MatIconModule,
    MatProgressBarModule,
    MatTooltipModule
  ],
  templateUrl: './secure-us-access-page.component.html',
  styleUrl: './secure-us-access-page.component.scss'
})
export class SecureUsAccessPageComponent implements OnDestroy {
  acknowledged = false;
  connecting = false;
  connected = false;
  progress = 0;
  statusMessage = 'Secure gateway ready';
  errorMessage = '';

  private progressTimer?: ReturnType<typeof setInterval>;

  constructor(
    private readonly carrierPortalService: CarrierPortalService,
    private readonly secureBrowserService: SecureBrowserService
  ) {}

  startConnection(): void {
    if (!this.acknowledged || this.connecting) {
      return;
    }

    // The window must be created directly from the user's click so that
    // browsers do not block it as an unsolicited pop-up.
    const secureWindow = window.open('about:blank', '_blank');

    if (!secureWindow) {
      this.errorMessage = 'Your browser blocked the secure window. Allow pop-ups for Nexcoverin and try again.';
      return;
    }

    try {
      secureWindow.opener = null;
      secureWindow.document.title = 'Nexcoverin Secure Connection';
      secureWindow.document.body.innerHTML = `
        <div style="font-family:Arial,sans-serif;display:grid;place-items:center;min-height:90vh;color:#17324d">
          <div style="text-align:center">
            <h2>Establishing secure connection...</h2>
            <p>Please wait while Nexcoverin validates your access.</p>
          </div>
        </div>`;
    } catch {
      // Navigation can continue even when the browser isolates the blank window.
    }

    this.clearTimer();
    this.connecting = true;
    this.connected = false;
    this.progress = 15;
    this.errorMessage = '';
    this.statusMessage = 'Validating secure access';
    this.startProgress();

    this.carrierPortalService.listActive()
      .pipe(
        switchMap((portals: CarrierPortal[]) => {
          const portal = this.resolveDefaultPortal(portals);
          if (!portal) {
            throw new Error('No active carrier portal is configured.');
          }

          this.statusMessage = `Preparing ${portal.displayName}`;
          return this.secureBrowserService.launchCarrierPortal(portal.id);
        }),
        finalize(() => {
          this.connecting = false;
          this.clearTimer();
        })
      )
      .subscribe({
        next: (response: SecureBrowserLaunchResponse) => {
          if (!response.launchUrl) {
            secureWindow.close();
            this.handleLaunchError('The secure browser service returned an empty launch URL.');
            return;
          }

          this.connected = true;
          this.progress = 100;
          this.statusMessage = `${response.portalName} opened through Nexcoverin Trust Connection`;
          secureWindow.location.replace(response.launchUrl);
        },
        error: (error) => {
          console.error('Error opening WorkSpaces Secure Browser', error);
          secureWindow.close();
          this.handleLaunchError(this.resolveErrorMessage(error));
        }
      });
  }

  resetConnection(): void {
    this.clearTimer();
    this.connecting = false;
    this.connected = false;
    this.progress = 0;
    this.errorMessage = '';
    this.statusMessage = 'Secure gateway ready';
  }

  ngOnDestroy(): void {
    this.clearTimer();
  }

  private resolveDefaultPortal(portals: CarrierPortal[]): CarrierPortal | undefined {
    if (!portals?.length) {
      return undefined;
    }

    // The popup was intentionally removed. The first active portal returned by
    // the backend (already ordered by sortOrder) becomes the direct destination.
    return portals[0];
  }

  private startProgress(): void {
    this.progressTimer = setInterval(() => {
      if (this.progress < 90) {
        this.progress += 5;
      }
    }, 250);
  }

  private handleLaunchError(message: string): void {
    this.connected = false;
    this.progress = 0;
    this.statusMessage = 'Secure connection unavailable';
    this.errorMessage = message;
  }

  private resolveErrorMessage(error: any): string {
    if (error?.status === 401) {
      return 'Your Nexcoverin session has expired. Sign in again and retry.';
    }
    if (error?.status === 403) {
      return 'You are not authorized to open the secure carrier connection.';
    }
    if (error?.status === 404) {
      return 'No active carrier portal was found for this connection.';
    }
    return error?.error?.message || error?.message || 'The secure connection could not be established.';
  }

  private clearTimer(): void {
    if (this.progressTimer) {
      clearInterval(this.progressTimer);
      this.progressTimer = undefined;
    }
  }
}
