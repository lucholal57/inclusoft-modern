import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  imports: [ButtonModule, CommonModule, InputTextModule, PasswordModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  private readonly formBuilder = inject(FormBuilder);
  errorMessage = '';
  submitting = false;
  readonly form = this.formBuilder.nonNullable.group({
    username: ['', Validators.required],
    password: ['', Validators.required]
  });

  constructor(private readonly authService: AuthService, private readonly router: Router) {}

  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.submitting = true;
    this.errorMessage = '';
    const { username, password } = this.form.getRawValue();
    this.authService.login(username, password).subscribe({
      next: (session) => this.router.navigateByUrl(session.mustChangePassword ? '/cambiar-contrasena' : '/'),
      error: (error) => { this.submitting = false; this.errorMessage = error.error?.message ?? 'No pudimos iniciar sesión.'; }
    });
  }
}
