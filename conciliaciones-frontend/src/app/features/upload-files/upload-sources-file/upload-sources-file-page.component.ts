import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpEventType } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSelectModule } from '@angular/material/select';

import { Carrier } from '../../../models/carrier.model';
import { PresignedUploadResponse } from '../../../models/upload-source-file.model';
import { UploadSourceFileService } from '../../../services/upload-source-file.service';
import { CarrierService } from '../../../services/carrier.service';
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
    MatSelectModule,
    MatSnackBarModule,
    PageHeaderComponent
  ],
  templateUrl: 'upload-sources-file-page.component.html',
  styleUrl: 'upload-sources-file-page.component.scss'
})
export class UploadSourcesFilePageComponent implements OnInit {
  selectedFile: File | null = null;
  folder = '';
  carriers: Carrier[] = [];
  selectedCarrierId: number | null = null;
  carriersLoading = false;
  carriersError = '';
  uploadInProgress = false;
  progress = 0;
  uploadCompleted = false;
  uploadError = '';
  uploadInfo: PresignedUploadResponse | null = null;

  constructor(
    private readonly uploadService: UploadSourceFileService,
    private readonly carrierService: CarrierService,
    private readonly snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadCarriers();
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedFile = input.files?.[0] ?? null;
    this.resetUploadState();
  }

  upload(): void {
    if (!this.selectedFile || !this.selectedCarrierId || this.uploadInProgress) {
      return;
    }

    this.resetUploadState();
    this.uploadInProgress = true;

    this.uploadService.generatePresignedUrl(this.selectedFile,this.selectedCarrierId,this.folder).subscribe({
      next: (response: PresignedUploadResponse) => {
        this.uploadInfo = response;
        this.startUpload(response, this.selectedFile as File);
      },
      error: (error: { error?: { message?: string } }) => {
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
            this.notifyUploadResult(response, file, true);
          }
        },
        error: () => {
          this.notifyUploadResult(
            response,
            file,
            false,
            'Ocurrió un error durante la carga del archivo en S3.'
          );
        }
      });
  }

  private notifyUploadResult(
    response: PresignedUploadResponse,
    file: File,
    success: boolean,
    errorMessage?: string
  ): void {
    this.uploadService
      .confirmUploadCallback({
        sourceFileId: response.sourceFileId,
        bucketName: response.bucketName,
        objectKey: response.objectKey,
        fileName: file.name,
        contentType: file.type || 'application/octet-stream',
        fileSizeBytes: file.size,
        success,
        errorMessage: errorMessage ?? null
      })
      .subscribe({
        next: () => {
          if (success) {
            this.progress = 100;
            this.uploadCompleted = true;
            this.uploadError = '';
            this.snackBar.open('Archivo cargado y confirmado exitosamente.', 'Cerrar', {
              duration: 4000
            });
            return;
          }

          this.handleError(errorMessage ?? 'Ocurrió un error durante la carga del archivo en S3.');
        },
        error: () => {
          const message = success
            ? 'El archivo subió a S3, pero falló la notificación callback.'
            : 'Falló la carga a S3 y también falló la notificación callback.';
          this.handleError(message);
        }
      });
  }

  getProgressColor(): string {
    if (this.progress <= 30) {
      return 'linear-gradient(90deg, #E53935, #FF7043)';
    }

    if (this.progress <= 50) {
      return 'linear-gradient(90deg, #FF6F61, #FF8A65)';
    }

    if (this.progress <= 80) {
      return 'linear-gradient(90deg, #FFA726, #FFCA28)';
    }

    return 'linear-gradient(90deg, #2E7D32, #66BB6A)';
  }

  getProgressStatusLabel(): string {
    if (this.progress <= 30) {
      return 'Iniciando cargue';
    }

    if (this.progress <= 50) {
      return 'Cargando archivo';
    }

    if (this.progress <= 80) {
      return 'Procesando transferencia';
    }

    return this.uploadCompleted ? 'Carga finalizada' : 'Finalizando cargue';
  }

  private loadCarriers(): void {
    this.carriersLoading = true;
    this.carriersError = '';

    this.carrierService.list().subscribe({
      next: (carriers) => {
        this.carriers = carriers.filter((carrier) => carrier.active !== false);
        this.carriersLoading = false;

        if (!this.carriers.length) {
          this.carriersError = 'No hay carriers activos configurados.';
        }
      },
      error: () => {
        this.carriers = [];
        this.selectedCarrierId = null;
        this.carriersLoading = false;
        this.carriersError = 'No fue posible cargar los carriers.';
        this.snackBar.open(this.carriersError, 'Cerrar', { duration: 5000 });
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
}
