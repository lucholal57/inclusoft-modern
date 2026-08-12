import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { apiConfig } from '../../core/api.config';
import { CreateWorkshop, Workshop, WorkshopDetail } from './workshop.model';

@Injectable({ providedIn: 'root' })
export class WorkshopsService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = `${apiConfig.baseUrl}/workshops`;
  findAll(search = ''): Observable<Workshop[]> { const params = search.trim() ? new HttpParams().set('search', search.trim()) : undefined; return this.http.get<Workshop[]>(this.endpoint, { params }); }
  findById(id: string): Observable<WorkshopDetail> { return this.http.get<WorkshopDetail>(`${this.endpoint}/${id}`); }
  create(workshop: CreateWorkshop): Observable<Workshop> { return this.http.post<Workshop>(this.endpoint, workshop); }
  update(id: string, workshop: CreateWorkshop): Observable<Workshop> { return this.http.put<Workshop>(`${this.endpoint}/${id}`, workshop); }
  replaceStudents(id: string, studentIds: string[]): Observable<WorkshopDetail> { return this.http.put<WorkshopDetail>(`${this.endpoint}/${id}/students`, { studentIds }); }
  replaceTeam(id: string, staffMemberIds: string[]): Observable<WorkshopDetail> { return this.http.put<WorkshopDetail>(`${this.endpoint}/${id}/team`, { staffMemberIds }); }
  deactivate(id: string): Observable<Workshop> { return this.http.patch<Workshop>(`${this.endpoint}/${id}/deactivate`, {}); }
  activate(id: string): Observable<Workshop> { return this.http.patch<Workshop>(`${this.endpoint}/${id}/activate`, {}); }
}