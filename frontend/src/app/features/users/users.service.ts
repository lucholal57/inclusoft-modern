import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { apiConfig } from '../../core/api.config';
import { AppUser, CreateUserRequest, UpdateUserRequest } from './user.model';

@Injectable({ providedIn: 'root' })
export class UsersService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = `${apiConfig.baseUrl}/users`;

  findAll(): Observable<AppUser[]> { return this.http.get<AppUser[]>(this.endpoint); }
  create(request: CreateUserRequest): Observable<AppUser> { return this.http.post<AppUser>(this.endpoint, request); }
  update(username: string, request: UpdateUserRequest): Observable<AppUser> { return this.http.put<AppUser>(`${this.endpoint}/${encodeURIComponent(username)}`, request); }
  deactivate(username: string): Observable<AppUser> { return this.http.patch<AppUser>(`${this.endpoint}/${encodeURIComponent(username)}/deactivate`, {}); }
  activate(username: string): Observable<AppUser> { return this.http.patch<AppUser>(`${this.endpoint}/${encodeURIComponent(username)}/activate`, {}); }
}