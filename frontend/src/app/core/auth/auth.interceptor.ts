import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const auth = inject(AuthService);
  const session = auth.session();
  const authenticatedRequest = session ? request.clone({ setHeaders: { Authorization: `Bearer ${session.token}` } }) : request;
  return next(authenticatedRequest);
};
