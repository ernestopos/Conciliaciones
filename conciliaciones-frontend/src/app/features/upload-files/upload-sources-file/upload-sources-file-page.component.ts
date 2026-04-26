import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpEventType } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

import { PresignedUploadResponse } from '../../../models/upload-source-file.model';
import { UploadSourceFileService } from '../../../services/upload-source-file.service';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-upload-sources-file-page',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressBarModule,
    MatSnackBarModule,
    PageHeaderComponent
  ],
  templateUrl: 'upload-sources-file-page.component.html',
  styleUrl: 'upload-sources-file-page.component.scss'
})
export class UploadSourcesFilePageComponent {
  selectedFile: File | null = null;
  folder = '';
  uploadInProgress = false;
  progress = 0;
  uploadCompleted = false;
  uploadError = '';
  uploadInfo: PresignedUploadResponse | null = null;

  constructor(
    private readonly uploadService: UploadSourceFileService,
    private readonly snackBar: MatSnackBar
  ) {}

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedFile = input.files?.[0] ?? null;
    this.resetUploadState();
  }

  upload(): void {
    if (!this.selectedFile || this.uploadInProgress) {
      return;
    }

    this.resetUploadState();
    this.uploadInProgress = true;

    this.uploadService.generatePresignedUrl(this.selectedFile, this.folder).subscribe({
      next: (response) => {
        this.uploadInfo = response;
        this.startUpload(response, this.selectedFile as File);
      },
      error: (error) => {
        this.handleError(error?.error?.message ?? 'No fue posible generar la URL prefirmada.');
      }
    });
  }

  private startUpload(response: PresignedUploadResponse, file: File): void {
    this.uploadService
      .uploadFile(response.presignedUrl, file)
      .pipe(finalize(() => (this.uploadInProgress = false)))
      .subscribe({
        next: (event) => {
          if (event.type === HttpEventType.UploadProgress) {
            const total = event.total ?? file.size;
            this.progress = total > 0 ? Math.round((100 * event.loaded) / total) : 0;
            return;
          }

          if (event.type === HttpEventType.Response) {
            this.progress = 100;
            this.uploadCompleted = true;
            this.snackBar.open('Archivo cargado exitosamente en S3.', 'Cerrar', {
              duration: 4000
            });
          }
        },
        error: () => {
          this.handleError('Ocurrió un error durante la carga del archivo en S3.');
        }
      });
  }

  private handleError(message: string): void {
    this.uploadInProgress = false;
    this.uploadCompleted = false;
    this.uploadError = message;
    this.snackBar.open(message, 'Cerrar', { duration: 5000 });
  }

  private resetUploadState(): void {
    this.progress = 0;
    this.uploadCompleted = false;
    this.uploadError = '';
    this.uploadInfo = null;
  }

  getProgressColor(): string {
    if (this.progress <= 30) return '#E53935'; // rojo
    if (this.progress <= 50) return '#FF6F61'; // salmón
    if (this.progress <= 80) return '#FFA726'; // mango
    return '#2E7D32'; // verde
    }
}