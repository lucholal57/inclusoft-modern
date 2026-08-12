import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { TagModule } from 'primeng/tag';
import { TextareaModule } from 'primeng/textarea';
import { AuthService } from '../../core/auth/auth.service';
import { AppNavigationComponent } from '../../shared/app-navigation/app-navigation.component';
import { Student, StudentHealthRecord } from '../students/student.model';
import { StudentsService } from '../students/students.service';

@Component({ selector: 'app-student-health', imports: [AppNavigationComponent, ButtonModule, CommonModule, InputTextModule, ReactiveFormsModule, RouterLink, TagModule, TextareaModule], templateUrl: './student-health.component.html', styleUrl: './student-health.component.css' })
export class StudentHealthComponent implements OnInit {
  private readonly route = inject(ActivatedRoute); private readonly students = inject(StudentsService); private readonly fb = inject(FormBuilder); readonly auth = inject(AuthService);
  student?: Student; loading = true; saving = false; error = ''; saveError = ''; saveSuccess = '';
  readonly form = this.fb.nonNullable.group({ medicalReferences: ['', Validators.maxLength(1000)], medications: ['', Validators.maxLength(1000)], allergies: ['', Validators.maxLength(1000)], healthInsurance: ['', Validators.maxLength(160)], treatingProfessionals: ['', Validators.maxLength(1000)], supportGuidelines: ['', Validators.maxLength(2000)], observations: ['', Validators.maxLength(2000)] });
  get canEdit(): boolean { return this.auth.session()?.role !== 'TEACHER'; }
  ngOnInit(): void { this.form.valueChanges.subscribe(() => { this.saveError = ''; this.saveSuccess = ''; }); const id = this.route.snapshot.paramMap.get('id'); if (id) this.load(id); }
  private load(id: string): void { this.students.findById(id).subscribe({ next: student => { this.student = student; this.students.findHealthRecord(id).subscribe({ next: health => { this.form.patchValue(this.values(health)); this.form.markAsPristine(); this.loading = false; }, error: () => { this.error = 'No pudimos cargar la información de salud.'; this.loading = false; } }); }, error: () => { this.error = 'No pudimos cargar la ficha del alumno.'; this.loading = false; } }); }
  save(): void { if (!this.student || this.form.invalid || !this.form.dirty) return; this.saving = true; this.saveError = ''; this.saveSuccess = ''; this.students.replaceHealthRecord(this.student.id, this.form.getRawValue()).subscribe({ next: () => { this.saving = false; this.form.markAsPristine(); this.saveSuccess = 'La información de salud y apoyos se guardó correctamente.'; }, error: error => { this.saving = false; this.saveError = error.error?.message ?? 'No pudimos guardar la información. Revisá los datos e intentá nuevamente.'; } }); }
  private values(health: StudentHealthRecord): { medicalReferences: string; medications: string; allergies: string; healthInsurance: string; treatingProfessionals: string; supportGuidelines: string; observations: string } { return { medicalReferences: health.medicalReferences ?? '', medications: health.medications ?? '', allergies: health.allergies ?? '', healthInsurance: health.healthInsurance ?? '', treatingProfessionals: health.treatingProfessionals ?? '', supportGuidelines: health.supportGuidelines ?? '', observations: health.observations ?? '' }; }
}
