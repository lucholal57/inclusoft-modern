import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { tap } from 'rxjs';
import { apiConfig } from '../api.config';
import { AuthSession } from './auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly storageKey = 'inclusoft.session';
  readonly session = signal<AuthSession | null>(this.readSession());

  constructor(private readonly http: HttpClient) {}

  login(username: string, password: string) {
    return this.http.post<AuthSession>(`${apiConfig.baseUrl}/auth/login`, { username, password }).pipe(
      tap((session) => {
        sessionStorage.setItem(this.storageKey, JSON.stringify(session));
        this.session.set(session);
      })
    );
  }

  changePassword(currentPassword: string, newPassword: string) {
    return this.http.post<AuthSession>(`${apiConfig.baseUrl}/auth/change-password`, { currentPassword, newPassword }).pipe(
      tap((session) => this.storeSession(session))
    );
  }

  logout(): void {
    sessionStorage.removeItem(this.storageKey);
    this.session.set(null);
  }

  private storeSession(session: AuthSession): void {
    sessionStorage.setItem(this.storageKey, JSON.stringify(session));
    this.session.set(session);
  }

  private readSession(): AuthSession | null {
    try {
      const stored = sessionStorage.getItem(this.storageKey);
      return stored ? JSON.parse(stored) as AuthSession : null;
    } catch {
      return null;
    }
  }
}
