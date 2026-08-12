import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const session = authService.session();
  if (!session) return router.createUrlTree(['/login']);
  return session.mustChangePassword ? router.createUrlTree(['/cambiar-contrasena']) : true;
};

export const passwordChangeGuard: CanActivateFn = () => {
  const session = inject(AuthService).session();
  const router = inject(Router);
  if (!session) return router.createUrlTree(['/login']);
  return session.mustChangePassword ? true : router.createUrlTree(['/']);
};