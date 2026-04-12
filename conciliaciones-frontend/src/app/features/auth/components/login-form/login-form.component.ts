import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../../../auth/services/auth.service';
import { CaptchaComponent } from '../../../../auth/components/captcha/captcha.component';

@Component({
  selector: 'app-login-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatSnackBarModule,
    CaptchaComponent
  ],
  templateUrl: './login-form.component.html',
  styleUrl: './login-form.component.scss'
})
export class LoginFormComponent {
  hidePassword = true;
  captchaValid = false;
  loading = false;

  readonly form = this.fb.group({
    username: ['', [Validators.required]],
    password: ['', [Validators.required]]
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly snackBar: MatSnackBar
  ) {}

  submit(): void {
    if (this.form.invalid || !this.captchaValid || this.loading) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.authService.login(this.form.getRawValue() as { username: string; password: string }).subscribe({
      next: (result) => {
        this.loading = false;
        if (!result.success) {
          this.snackBar.open(result.errorMessage ?? 'No fue posible iniciar sesión', 'Cerrar', {
            panelClass: ['error-snackbar'],
            duration: 3000
          });
          return;
        }

        this.snackBar.open(`Bienvenido ${result.user?.name ?? ''}`, 'Cerrar', { duration: 2200 });
        this.router.navigate(['/dashboard']);
      },
      error: () => {
        this.loading = false;
        this.snackBar.open('Error al intentarse loguear a la aplicación', 'Cerrar', { duration: 3200 });
      }
    });
  }
}
