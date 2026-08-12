import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ConfirmationService } from 'primeng/api';
import { FormArray, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { DrawerModule } from 'primeng/drawer';
import { InputTextModule } from 'primeng/inputtext';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { TextareaModule } from 'primeng/textarea';
import { AuthService } from '../../core/auth/auth.service';
import { AppNavigationComponent } from '../../shared/app-navigation/app-navigation.component';
import { Workshop, WorkshopSchedule } from './workshop.model';
import { WorkshopsService } from './workshops.service';

@Component({
  selector: 'app-workshops',
  imports: [AppNavigationComponent, ButtonModule, CommonModule, ConfirmDialogModule, DrawerModule, FormsModule, InputTextModule, ReactiveFormsModule, RouterLink, TableModule, TagModule, TextareaModule],
  templateUrl: './workshops.component.html',
  styleUrl: './workshops.component.css'
})
export class WorkshopsComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly workshopsService = inject(WorkshopsService);
  readonly authService = inject(AuthService);
  private readonly confirmationService = inject(ConfirmationService);
  editingWorkshop: Workshop | null = null;
  readonly weekDays = [
    { value: 'MONDAY', label: 'Lunes' }, { value: 'TUESDAY', label: 'Martes' },
    { value: 'WEDNESDAY', label: 'Miércoles' }, { value: 'THURSDAY', label: 'Jueves' },
    { value: 'FRIDAY', label: 'Viernes' }, { value: 'SATURDAY', label: 'Sábado' }
  ];
  workshops: Workshop[] = [];
  loading = true;
  saving = false;
  showForm = false;
  errorMessage = '';
  searchTerm = '';
  private searchDelay?: ReturnType<typeof setTimeout>;
  readonly form = this.formBuilder.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(120)]],
    description: ['', Validators.maxLength(500)],
    capacity: [10, [Validators.required, Validators.min(1), Validators.max(500)]],
    schedules: this.formBuilder.array([this.createSchedule()], Validators.minLength(1))
  });

  ngOnInit(): void { this.loadWorkshops(); }
  get canManageWorkshops(): boolean { return this.authService.session()?.role !== 'TEACHER'; }
  get schedules(): FormArray<FormGroup> { return this.form.controls.schedules as FormArray<FormGroup>; }
  workshopIcon(name: string): string {
    const normalizedName = name.toLocaleLowerCase();
    if (normalizedName.includes('música') || normalizedName.includes('musica')) return 'pi pi-volume-up';
    if (normalizedName.includes('arte') || normalizedName.includes('pintura')) return 'pi pi-palette';
    if (normalizedName.includes('deporte') || normalizedName.includes('educación física')) return 'pi pi-heart';
    return 'pi pi-compass';
  }
  cardTone(name: string): string {
    const normalizedName = name.toLocaleLowerCase();
    if (normalizedName.includes('música') || normalizedName.includes('musica') || normalizedName.includes('radio')) return 'tone-sky';
    if (normalizedName.includes('arte') || normalizedName.includes('pintura') || normalizedName.includes('plástica') || normalizedName.includes('plastica')) return 'tone-amber';
    if (normalizedName.includes('danza')) return 'tone-rose';
    if (normalizedName.includes('panificación') || normalizedName.includes('panificacion')) return 'tone-orange';
    if (normalizedName.includes('tecnología') || normalizedName.includes('tecnologia')) return 'tone-cyan';
    if (normalizedName.includes('vivero')) return 'tone-green';
    return 'tone-slate';
  }
  dayLabel(dayOfWeek: string): string {
    const dayLabels: Record<string, string> = { MONDAY: 'Lun', TUESDAY: 'Mar', WEDNESDAY: 'Mié', THURSDAY: 'Jue', FRIDAY: 'Vie', SATURDAY: 'Sáb', SUNDAY: 'Dom' };
    return dayLabels[dayOfWeek] ?? dayOfWeek;
  }
  onSearchChange(): void { if (this.searchDelay) clearTimeout(this.searchDelay); this.searchDelay = setTimeout(() => this.loadWorkshops(), 280); }
  loadWorkshops(): void {
    this.loading = true; this.errorMessage = '';
    this.workshopsService.findAll(this.searchTerm).subscribe({
      next: (workshops) => { this.workshops = workshops; this.loading = false; },
      error: () => { this.errorMessage = 'No pudimos cargar los talleres. Verificá que el backend esté iniciado.'; this.loading = false; }
    });
  }
  openForm(): void { this.editingWorkshop = null; this.form.reset({ name: '', description: '', capacity: 10 }); this.schedules.clear(); this.addSchedule(); this.errorMessage = ''; this.showForm = true; }
  closeForm(): void { this.showForm = false; this.editingWorkshop = null; this.form.reset(); }
  openEdit(workshop: Workshop): void { this.editingWorkshop = workshop; this.form.reset({ name: workshop.name, description: workshop.description ?? '', capacity: workshop.capacity }); this.schedules.clear(); workshop.schedules.forEach((schedule) => this.schedules.push(this.formBuilder.nonNullable.group({ dayOfWeek: [schedule.dayOfWeek, Validators.required], startTime: [schedule.startTime, Validators.required], endTime: [schedule.endTime, Validators.required], location: [schedule.location ?? '', Validators.maxLength(120)] }))); this.errorMessage = ''; this.showForm = true; }
  confirmStatus(workshop: Workshop): void { const deactivate = workshop.status === 'ACTIVE'; this.confirmationService.confirm({ header: deactivate ? 'Dar de baja taller' : 'Reactivar taller', message: deactivate ? `¿Querés dar de baja el taller ${workshop.name}? Podrás reactivarlo cuando sea necesario.` : `¿Querés reactivar el taller ${workshop.name}?`, icon: deactivate ? 'pi pi-exclamation-triangle' : 'pi pi-check-circle', acceptLabel: deactivate ? 'Dar de baja' : 'Reactivar', rejectLabel: 'Cancelar', acceptButtonStyleClass: deactivate ? 'p-button-danger' : '', accept: () => (deactivate ? this.workshopsService.deactivate(workshop.id) : this.workshopsService.activate(workshop.id)).subscribe({ next: () => this.loadWorkshops(), error: (error) => this.errorMessage = error.error?.message ?? 'No pudimos actualizar el estado.' }) }); }
  addSchedule(): void { this.schedules.push(this.createSchedule()); }
  removeSchedule(index: number): void { if (this.schedules.length > 1) this.schedules.removeAt(index); }
  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.saving = true; this.errorMessage = '';
    const value = this.form.getRawValue();
    const payload = { name: value.name, description: value.description || null, capacity: value.capacity, schedules: value.schedules.map((schedule) => ({ dayOfWeek: schedule['dayOfWeek'] as string, startTime: schedule['startTime'] as string, endTime: schedule['endTime'] as string, location: (schedule['location'] as string) || null })) };
    const request = this.editingWorkshop ? this.workshopsService.update(this.editingWorkshop.id, payload) : this.workshopsService.create(payload);
    request.subscribe({
      next: () => { this.saving = false; this.closeForm(); this.loadWorkshops(); },
      error: (error) => { this.saving = false; this.errorMessage = error.error?.message ?? 'No pudimos guardar el taller.'; }
    });
  }
  private createSchedule(): FormGroup { return this.formBuilder.nonNullable.group({ dayOfWeek: ['MONDAY', Validators.required], startTime: ['', Validators.required], endTime: ['', Validators.required], location: ['', Validators.maxLength(120)] }); }
}



