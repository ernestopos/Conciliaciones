import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Subscription, timer } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ProcessingExecutionService } from '../../../services/processing-execution.service';
import { ProcessingExecutionDetailModel, ScheduledTaskStepModel } from '../../../models/processing-execution.model';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { ValidationExecutionDetailDialogComponent } from './validation-execution-detail-dialog.component';

@Component({
  selector: 'app-processing-execution-detail-page',
  standalone: true,
  imports: [CommonModule, RouterModule, MatButtonModule, MatIconModule, MatDialogModule, MatSnackBarModule, LoadingSpinnerComponent, EmptyStateComponent, PageHeaderComponent],
  templateUrl: './processing-execution-detail-page.component.html',
  styleUrl: './processing-execution-detail-page.component.scss'
})
export class ProcessingExecutionDetailPageComponent implements OnInit, OnDestroy {
  private static readonly REFRESH_INTERVAL_MS = 5000;

  loading = true;
  detail?: ProcessingExecutionDetailModel;

  private refreshSubscription?: Subscription;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly service: ProcessingExecutionService,
    private readonly dialog: MatDialog,
    private readonly snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.refreshSubscription = timer(0, ProcessingExecutionDetailPageComponent.REFRESH_INTERVAL_MS)
      .pipe(switchMap(() => this.service.getDetail(id)))
      .subscribe({
        next: (data) => {
          this.detail = data;
          this.loading = false;

          if (this.isExecutionFinished(data)) {
            this.refreshSubscription?.unsubscribe();
          }
        },
        error: () => {
          this.detail = undefined;
          this.loading = false;
        }
      });
  }

  ngOnDestroy(): void {
    this.refreshSubscription?.unsubscribe();
  }

  stepClass(step: ScheduledTaskStepModel): string {
    const status = this.normalizeStatus(step.statusName);

    if (this.isCompletedStatus(status)) {
      return 'completed';
    }

    if (this.isRunningStatus(status)) {
      return 'running';
    }

    if (this.isFailedStatus(status)) {
      return 'failed';
    }

    return 'pending';
  }



  openValidationDetail(): void {
    if (!this.detail?.id) {
      return;
    }

    this.service.getValidationDetails(this.detail.id).subscribe({
      next: (rows) => {
        this.dialog.open(ValidationExecutionDetailDialogComponent, {
          data: { rows },
          width: '95vw',
          maxWidth: '1200px'
        });
      },
      error: () => {
        this.snackBar.open('No fue posible cargar el detalle de validación.', 'Cerrar', {
          duration: 4000
        });
      }
    });
  }

  canShowValidationDetail(task: ScheduledTaskStepModel): boolean {
    const taskType = this.normalizeStatus(task.taskTypeName);
    const taskStatus = this.normalizeStatus(task.statusName);

    return taskType === 'START_VALIDATE_DATA'
      && (this.isCompletedStatus(taskStatus) || this.isFailedStatus(taskStatus));
  }

  display(value: unknown): string {
    if (value === null || value === undefined || value === '') return '—';
    if (typeof value === 'boolean') return value ? 'Sí' : 'No';
    return String(value);
  }

  private isExecutionFinished(detail: ProcessingExecutionDetailModel): boolean {
    const executionStatus = this.normalizeStatus(detail.statusName);
    const executionFinished = this.isCompletedStatus(executionStatus) || this.isFailedStatus(executionStatus);

    const allTasksFinished = detail.tasks.every((task) => {
      const taskStatus = this.normalizeStatus(task.statusName);
      return this.isCompletedStatus(taskStatus) || this.isFailedStatus(taskStatus);
    });

    return executionFinished && allTasksFinished;
  }

  private normalizeStatus(status?: string): string {
    return (status ?? '').trim().toUpperCase();
  }

  private isCompletedStatus(status: string): boolean {
    return status.includes('COMPLETED')
      || status.includes('SUCCESS')
      || status.includes('EXECUTED')
      || status.includes('FINALIZ');
  }

  private isRunningStatus(status: string): boolean {
    return status.includes('PROCESS')
      || status.includes('PROGRESS')
      || status.includes('RUNNING')
      || status.includes('EJEC');
  }

  private isFailedStatus(status: string): boolean {
    return status.includes('ERROR')
      || status.includes('FAILED')
      || status.includes('FAIL')
      || status.includes('REJECTED');
  }
}
