import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ConfirmationService } from 'primeng/api';
import { RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { DatePickerModule } from 'primeng/datepicker';
import { DrawerModule } from 'primeng/drawer';
import { InputTextModule } from 'primeng/inputtext';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { AuthService } from '../../core/auth/auth.service';
import { AppNavigationComponent } from '../../shared/app-navigation/app-navigation.component';
import { Student } from './student.model';
import { StudentsService } from './students.service';

@Component({
  selector: 'app-students',
  imports: [AppNavigationComponent, ButtonModule, CommonModule, ConfirmDialogModule, DatePickerModule, DrawerModule, FormsModule, InputTextModule, ReactiveFormsModule, RouterLink, TableModule, TagModule],
  templateUrl: './students.component.html', styleUrl: './students.component.css'
})
export class StudentsComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly studentsService = inject(StudentsService);
  private readonly confirmationService = inject(ConfirmationService);
  readonly authService = inject(AuthService);
  readonly argentineProvinces = ['Buenos Aires', 'Catamarca', 'Chaco', 'Chubut', 'Ciudad Autónoma de Buenos Aires', 'Córdoba', 'Corrientes', 'Entre Ríos', 'Formosa', 'Jujuy', 'La Pampa', 'La Rioja', 'Mendoza', 'Misiones', 'Neuquén', 'Río Negro', 'Salta', 'San Juan', 'San Luis', 'Santa Cruz', 'Santa Fe', 'Santiago del Estero', 'Tierra del Fuego', 'Tucumán'];
  readonly provinceOptions = this.argentineProvinces;
  students: Student[] = [];
  loading = true;
  saving = false;
  showForm = false;
  editingStudent: Student | null = null;
  errorMessage = '';
  searchTerm = '';
  private searchDelay?: ReturnType<typeof setTimeout>;
  readonly form = this.formBuilder.nonNullable.group({ firstName: ['', [Validators.required, Validators.maxLength(100)]], lastName: ['', [Validators.required, Validators.maxLength(100)]], documentNumber: ['', [Validators.required, Validators.pattern(/^\d{7,9}$/)]], phoneNumber: ['', Validators.maxLength(30)], birthDate: [null as Date | null], birthPlace: [null as string | null], address: ['', Validators.maxLength(200)] });

  ngOnInit(): void { this.loadStudents(); }
  get canManageStudents(): boolean { return this.authService.session()?.role !== 'TEACHER'; }
  onSearchChange(): void { if (this.searchDelay) clearTimeout(this.searchDelay); this.searchDelay = setTimeout(() => this.loadStudents(), 280); }
  loadStudents(): void { this.loading = true; this.errorMessage = ''; this.studentsService.findAll(this.searchTerm).subscribe({ next: (students) => { this.students = students; this.loading = false; }, error: () => { this.errorMessage = 'No pudimos cargar los alumnos. Verificá que el backend esté iniciado.'; this.loading = false; } }); }
  openForm(): void { this.editingStudent = null; this.form.reset({ firstName: '', lastName: '', documentNumber: '', phoneNumber: '', birthDate: null, birthPlace: null, address: '' }); this.errorMessage = ''; this.showForm = true; }
  openEdit(student: Student): void { this.editingStudent = student; this.form.reset({ firstName: student.firstName, lastName: student.lastName, documentNumber: student.documentNumber, phoneNumber: student.phoneNumber ?? '', birthDate: student.birthDate ? new Date(`${student.birthDate}T00:00:00`) : null, birthPlace: student.birthPlace ?? null, address: student.address ?? '' }); this.errorMessage = ''; this.showForm = true; }
  closeForm(): void { this.showForm = false; this.editingStudent = null; this.form.reset(); }
  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.saving = true; this.errorMessage = '';
    const value = this.form.getRawValue();
    const payload = { ...value, phoneNumber: value.phoneNumber || null, birthDate: value.birthDate ? value.birthDate.toISOString().slice(0, 10) : null, birthPlace: value.birthPlace || null, address: value.address || null };
    const request = this.editingStudent ? this.studentsService.update(this.editingStudent.id, payload) : this.studentsService.create(payload);
    request.subscribe({ next: () => { this.saving = false; this.closeForm(); this.loadStudents(); }, error: (error) => { this.saving = false; this.errorMessage = error.error?.message ?? 'No pudimos guardar el alumno.'; } });
  }
  confirmStatus(student: Student): void {
    const deactivate = student.status === 'ACTIVE';
    this.confirmationService.confirm({ header: deactivate ? 'Dar de baja alumno' : 'Reactivar alumno', message: deactivate ? `¿Querés dar de baja a ${student.lastName}, ${student.firstName}? Podrás reactivarlo cuando sea necesario.` : `¿Querés reactivar a ${student.lastName}, ${student.firstName}?`, icon: deactivate ? 'pi pi-exclamation-triangle' : 'pi pi-check-circle', acceptLabel: deactivate ? 'Dar de baja' : 'Reactivar', rejectLabel: 'Cancelar', acceptButtonStyleClass: deactivate ? 'p-button-danger' : '', accept: () => (deactivate ? this.studentsService.deactivate(student.id) : this.studentsService.activate(student.id)).subscribe({ next: () => this.loadStudents(), error: (error) => this.errorMessage = error.error?.message ?? 'No pudimos actualizar el estado.' }) });
  }
}
