import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { apiConfig } from '../../core/api.config';
import { CreateStudent, Student, StudentAuthorization, StudentContact, StudentContactRequest, StudentHealthRecord } from './student.model';

@Injectable({ providedIn: 'root' })
export class StudentsService {
  private readonly endpoint = `${apiConfig.baseUrl}/students`;
  constructor(private readonly http: HttpClient) {}
  findAll(search = ''): Observable<Student[]> { const params = search.trim() ? new HttpParams().set('search', search.trim()) : undefined; return this.http.get<Student[]>(this.endpoint, { params }); }
  findById(id: string): Observable<Student> { return this.http.get<Student>(`${this.endpoint}/${id}`); }
  create(student: CreateStudent): Observable<Student> { return this.http.post<Student>(this.endpoint, student); }
  update(id: string, student: CreateStudent): Observable<Student> { return this.http.put<Student>(`${this.endpoint}/${id}`, student); }
  findContacts(id: string): Observable<StudentContact[]> { return this.http.get<StudentContact[]>(`${this.endpoint}/${id}/contacts`); }
  replaceContacts(id: string, contacts: StudentContactRequest[]): Observable<StudentContact[]> { return this.http.put<StudentContact[]>(`${this.endpoint}/${id}/contacts`, contacts); }
  findHealthRecord(id: string): Observable<StudentHealthRecord> { return this.http.get<StudentHealthRecord>(`${this.endpoint}/${id}/health`); }
  replaceHealthRecord(id: string, healthRecord: StudentHealthRecord): Observable<StudentHealthRecord> { return this.http.put<StudentHealthRecord>(`${this.endpoint}/${id}/health`, healthRecord); }
  findAuthorizations(id: string): Observable<StudentAuthorization> { return this.http.get<StudentAuthorization>(`${this.endpoint}/${id}/authorizations`); }
  replaceAuthorizations(id: string, authorizations: StudentAuthorization): Observable<StudentAuthorization> { return this.http.put<StudentAuthorization>(`${this.endpoint}/${id}/authorizations`, authorizations); }
  deactivate(id: string): Observable<Student> { return this.http.patch<Student>(`${this.endpoint}/${id}/deactivate`, {}); }
  activate(id: string): Observable<Student> { return this.http.patch<Student>(`${this.endpoint}/${id}/activate`, {}); }
}
