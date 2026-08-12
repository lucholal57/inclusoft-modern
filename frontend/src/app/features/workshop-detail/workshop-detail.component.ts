import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { DrawerModule } from 'primeng/drawer';
import { MessageModule } from 'primeng/message';
import { TagModule } from 'primeng/tag';
import { AuthService } from '../../core/auth/auth.service';
import { AppNavigationComponent } from '../../shared/app-navigation/app-navigation.component';
import { StaffMember } from '../staff/staff.model';
import { StaffService } from '../staff/staff.service';
import { Student } from '../students/student.model';
import { StudentsService } from '../students/students.service';
import { WorkshopDetail } from '../workshops/workshop.model';
import { WorkshopsService } from '../workshops/workshops.service';

@Component({ selector: 'app-workshop-detail', imports: [AppNavigationComponent, ButtonModule, CommonModule, DrawerModule, MessageModule, RouterLink, TagModule], templateUrl: './workshop-detail.component.html', styleUrl: './workshop-detail.component.css' })
export class WorkshopDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute); private readonly workshopsService = inject(WorkshopsService); private readonly studentsService = inject(StudentsService); private readonly staffService = inject(StaffService); readonly authService = inject(AuthService);
  workshop?: WorkshopDetail; availableStudents: Student[] = []; availableStaff: StaffMember[] = []; selectedStudentIds = new Set<string>(); selectedStaffIds = new Set<string>(); showStudentManager = false; showTeamManager = false; savingStudents = false; savingTeam = false; loading = true; errorMessage = ''; assignmentError = '';
  readonly dayLabels: Record<string, string> = { MONDAY: 'Lunes', TUESDAY: 'Martes', WEDNESDAY: 'Miércoles', THURSDAY: 'Jueves', FRIDAY: 'Viernes', SATURDAY: 'Sábado', SUNDAY: 'Domingo' };
  ngOnInit(): void { const id = this.route.snapshot.paramMap.get('id'); if (id) this.loadWorkshop(id); }
  get canManage(): boolean { return this.authService.session()?.role !== 'TEACHER'; }
  private loadWorkshop(id: string): void { this.loading = true; this.workshopsService.findById(id).subscribe({ next: (workshop) => { this.workshop = workshop; this.loading = false; }, error: () => { this.errorMessage = 'No pudimos cargar este taller.'; this.loading = false; } }); }
  openStudentManager(): void { if (!this.workshop) return; this.assignmentError = ''; this.selectedStudentIds = new Set(this.workshop.students.map((student) => student.id)); this.studentsService.findAll().subscribe({ next: (students) => { this.availableStudents = students.filter((student) => student.status === 'ACTIVE'); this.showStudentManager = true; }, error: () => this.errorMessage = 'No pudimos cargar los alumnos disponibles.' }); }
  openTeamManager(): void { if (!this.workshop) return; this.assignmentError = ''; this.selectedStaffIds = new Set(this.workshop.teamMembers.map((member) => member.id)); this.staffService.findAll().subscribe({ next: (staff) => { this.availableStaff = staff.filter((member) => member.active); this.showTeamManager = true; }, error: () => this.errorMessage = 'No pudimos cargar el personal disponible.' }); }
  toggleStudent(id: string, checked: boolean): void { checked ? this.selectedStudentIds.add(id) : this.selectedStudentIds.delete(id); }
  toggleStaff(id: string, checked: boolean): void { checked ? this.selectedStaffIds.add(id) : this.selectedStaffIds.delete(id); }
  saveStudents(): void { if (!this.workshop) return; this.assignmentError = ''; this.savingStudents = true; this.workshopsService.replaceStudents(this.workshop.id, [...this.selectedStudentIds]).subscribe({ next: (workshop) => { this.workshop = workshop; this.savingStudents = false; this.showStudentManager = false; }, error: (error) => { this.savingStudents = false; this.assignmentError = error.error?.message ?? 'No pudimos actualizar los alumnos del taller.'; } }); }
  saveTeam(): void { if (!this.workshop) return; this.assignmentError = ''; this.savingTeam = true; this.workshopsService.replaceTeam(this.workshop.id, [...this.selectedStaffIds]).subscribe({ next: (workshop) => { this.workshop = workshop; this.savingTeam = false; this.showTeamManager = false; }, error: (error) => { this.savingTeam = false; this.assignmentError = error.error?.message ?? 'No pudimos actualizar el equipo del taller.'; } }); }
  profileLabel(profile: string): string { return ({ TEACHER: 'Docente', THERAPEUTIC_SUPPORT: 'Acompañante terapéutico', ASSISTANT: 'Auxiliar', OTHER: 'Otro' } as Record<string, string>)[profile] ?? profile; }
  tone(name: string): string { const normalized = name.toLocaleLowerCase(); if (normalized.includes('música') || normalized.includes('musica') || normalized.includes('radio')) return 'tone-sky'; if (normalized.includes('arte') || normalized.includes('pintura') || normalized.includes('plástica') || normalized.includes('plastica')) return 'tone-amber'; if (normalized.includes('danza')) return 'tone-rose'; if (normalized.includes('panificación') || normalized.includes('panificacion')) return 'tone-orange'; if (normalized.includes('tecnología') || normalized.includes('tecnologia')) return 'tone-cyan'; if (normalized.includes('vivero')) return 'tone-green'; return 'tone-slate'; }
  icon(name: string): string { const normalized = name.toLocaleLowerCase(); if (normalized.includes('música') || normalized.includes('musica')) return 'pi pi-volume-up'; if (normalized.includes('arte') || normalized.includes('pintura')) return 'pi pi-palette'; if (normalized.includes('deporte') || normalized.includes('educación física')) return 'pi pi-heart'; return 'pi pi-compass'; }
}
