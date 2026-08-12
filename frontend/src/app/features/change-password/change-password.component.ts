import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-change-password',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './change-password.component.html',
  styleUrl: './change-password.component.css'
})
export class ChangePasswordComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  saving = false;
  errorMessage = '';
  readonly form = this.formBuilder.nonNullable.group({
    currentPassword: ['', Validators.required],
    newPassword: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(100)]],
    confirmation: ['', Validators.required]
  });

  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    const { currentPassword, newPassword, confirmation } = this.form.getRawValue();
    if (newPassword !== confirmation) { this.errorMessage = 'Las contraseñas nuevas no coinciden.'; return; }
    this.saving = true;
    this.errorMessage = '';
    this.authService.changePassword(currentPassword, newPassword).subscribe({
      next: () => this.router.navigateByUrl('/'),
      error: (error) => { this.saving = false; this.errorMessage = error.error?.message ?? 'No pudimos actualizar la contraseña.'; }
    });
  }
}