import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { DatePickerModule } from 'primeng/datepicker';
import { InputTextModule } from 'primeng/inputtext';
import { TagModule } from 'primeng/tag';
import { TextareaModule } from 'primeng/textarea';
import { AuthService } from '../../core/auth/auth.service';
import { AppNavigationComponent } from '../../shared/app-navigation/app-navigation.component';
import { Student, StudentAuthorization } from '../students/student.model';
import { StudentsService } from '../students/students.service';

@Component({ selector: 'app-student-authorizations', imports: [AppNavigationComponent, ButtonModule, CheckboxModule, CommonModule, DatePickerModule, InputTextModule, ReactiveFormsModule, RouterLink, TagModule, TextareaModule], templateUrl: './student-authorizations.component.html', styleUrl: './student-authorizations.component.css' })
export class StudentAuthorizationsComponent implements OnInit {
  private readonly route = inject(ActivatedRoute); private readonly students = inject(StudentsService); private readonly fb = inject(FormBuilder); readonly auth = inject(AuthService);
  student?: Student; loading = true; saving = false; error = ''; saveError = ''; saveSuccess = '';
  readonly form = this.fb.nonNullable.group({ imageUseAuthorized: false, localOutingsAuthorized: false, medicalEmergencyAuthorized: false, dataSharingAuthorized: false, authorizedBy: ['', Validators.maxLength(120)], authorizationDate: [null as Date | null], observations: ['', Validators.maxLength(2000)] });
  get canEdit(): boolean { return this.auth.session()?.role !== 'TEACHER'; }
  ngOnInit(): void { this.form.valueChanges.subscribe(() => { this.saveError = ''; this.saveSuccess = ''; }); if (!this.canEdit) this.form.disable(); const id = this.route.snapshot.paramMap.get('id'); if (id) this.load(id); }
  private load(id: string): void { this.students.findById(id).subscribe({ next: student => { this.student = student; this.students.findAuthorizations(id).subscribe({ next: authorizations => { this.form.patchValue({ ...authorizations, authorizedBy: authorizations.authorizedBy ?? '', observations: authorizations.observations ?? '', authorizationDate: authorizations.authorizationDate ? new Date(`${authorizations.authorizationDate}T12:00:00`) : null }); this.form.markAsPristine(); this.loading = false; }, error: () => { this.error = 'No pudimos cargar las autorizaciones del alumno.'; this.loading = false; } }); }, error: () => { this.error = 'No pudimos cargar la ficha del alumno.'; this.loading = false; } }); }
  save(): void { if (!this.student || this.form.invalid || !this.form.dirty) return; this.saving = true; this.saveError = ''; this.saveSuccess = ''; const raw = this.form.getRawValue(); const authorizations: StudentAuthorization = { ...raw, authorizedBy: raw.authorizedBy || null, authorizationDate: raw.authorizationDate ? this.toDate(raw.authorizationDate) : null, observations: raw.observations || null }; this.students.replaceAuthorizations(this.student.id, authorizations).subscribe({ next: () => { this.saving = false; this.form.markAsPristine(); this.saveSuccess = 'Las autorizaciones se guardaron correctamente.'; }, error: error => { this.saving = false; this.saveError = error.error?.message ?? 'No pudimos guardar las autorizaciones. Revisá los datos e intentá nuevamente.'; } }); }
  private toDate(date: Date): string { return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`; }
}
