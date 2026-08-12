import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { InputTextModule } from 'primeng/inputtext';
import { TagModule } from 'primeng/tag';
import { AuthService } from '../../core/auth/auth.service';
import { AppNavigationComponent } from '../../shared/app-navigation/app-navigation.component';
import { Student, StudentContact, StudentContactRequest } from '../students/student.model';
import { StudentsService } from '../students/students.service';

@Component({ selector: 'app-student-file', imports: [AppNavigationComponent, ButtonModule, CheckboxModule, CommonModule, InputTextModule, ReactiveFormsModule, RouterLink, TagModule], templateUrl: './student-file.component.html', styleUrl: './student-file.component.css' })
export class StudentFileComponent implements OnInit {
  private readonly route = inject(ActivatedRoute); private readonly students = inject(StudentsService); private readonly fb = inject(FormBuilder); readonly auth = inject(AuthService);
  student?: Student; loading = true; saving = false; error = ''; saveError = ''; saveSuccess = '';
  readonly form = this.fb.nonNullable.group({ contacts: this.fb.array<FormGroup>([]) });
  get contacts(): FormArray<FormGroup> { return this.form.controls.contacts; }
  get canEdit(): boolean { return this.auth.session()?.role !== 'TEACHER'; }
  ngOnInit(): void { this.form.valueChanges.subscribe(() => { this.saveError = ''; this.saveSuccess = ''; }); const id = this.route.snapshot.paramMap.get('id'); if (id) this.load(id); }
  private load(id: string): void { this.students.findById(id).subscribe({ next: student => { this.student = student; this.students.findContacts(id).subscribe({ next: contacts => { contacts.forEach(contact => this.contacts.push(this.contactGroup(contact))); this.form.markAsPristine(); this.loading = false; }, error: () => { this.error = 'No pudimos cargar los contactos del alumno.'; this.loading = false; } }); }, error: () => { this.error = 'No pudimos cargar la ficha del alumno.'; this.loading = false; } }); }
  addContact(): void { this.contacts.push(this.contactGroup()); this.form.markAsDirty(); }
  removeContact(index: number): void { this.contacts.removeAt(index); this.form.markAsDirty(); }
  save(): void { if (!this.student || this.form.invalid || !this.contacts.length || !this.form.dirty) { this.form.markAllAsTouched(); return; } this.saving = true; this.saveError = ''; this.saveSuccess = ''; const contacts: StudentContactRequest[] = this.contacts.getRawValue().map(contact => ({ fullName: contact['fullName'], relationship: contact['relationship'], phoneNumber: contact['phoneNumber'], email: contact['email'] || null, responsible: contact['responsible'], emergencyContact: contact['emergencyContact'] })); this.students.replaceContacts(this.student.id, contacts).subscribe({ next: () => { this.saving = false; this.form.markAsPristine(); this.saveSuccess = 'Los contactos se guardaron correctamente.'; }, error: error => { this.saving = false; this.saveError = error.error?.message ?? 'No pudimos guardar los contactos. Revisá los datos e intentá nuevamente.'; } }); }
  private contactGroup(contact?: StudentContact): FormGroup { return this.fb.nonNullable.group({ fullName: [contact?.fullName ?? '', [Validators.required, Validators.maxLength(120)]], relationship: [contact?.relationship ?? '', [Validators.required, Validators.maxLength(80)]], phoneNumber: [contact?.phoneNumber ?? '', [Validators.required, Validators.maxLength(30)]], email: [contact?.email ?? '', [Validators.email, Validators.maxLength(120)]], responsible: [contact?.responsible ?? false], emergencyContact: [contact?.emergencyContact ?? false] }); }
}
