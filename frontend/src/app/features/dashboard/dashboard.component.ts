import { CommonModule, DatePipe } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { AuthService } from '../../core/auth/auth.service';
import { AppNavigationComponent } from '../../shared/app-navigation/app-navigation.component';
import { StaffService } from '../staff/staff.service';
import { StudentsService } from '../students/students.service';
import { WorkshopsService } from '../workshops/workshops.service';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, DatePipe, RouterLink, AppNavigationComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  private readonly studentsService = inject(StudentsService);
  private readonly staffService = inject(StaffService);
  private readonly workshopsService = inject(WorkshopsService);
  readonly authService = inject(AuthService);
  readonly today = new Date();
  studentCount: number | null = null;
  activeStudentCount: number | null = null;
  activeStaffCount: number | null = null;
  staffCount: number | null = null;
  activeWorkshopCount: number | null = null;
  totalWorkshopCapacity: number | null = null;
  loading = true;

  get canManageInstitute(): boolean { return this.authService.session()?.role !== 'TEACHER'; }
  get canManageUsers(): boolean { const role = this.authService.session()?.role; return role === 'ADMIN' || role === 'DIRECTOR'; }
  get greetingName(): string { return this.authService.session()?.displayName?.split(' ')[0] ?? 'bienvenido'; }

  ngOnInit(): void {
    if (!this.canManageInstitute) { this.loading = false; return; }
    forkJoin({ students: this.studentsService.findAll(), staff: this.staffService.findAll(), workshops: this.workshopsService.findAll() }).subscribe({
      next: ({ students, staff, workshops }) => {
        this.studentCount = students.length;
        this.activeStudentCount = students.filter(student => student.status === 'ACTIVE').length;
        this.staffCount = staff.length;
        this.activeStaffCount = staff.filter(member => member.active).length;
        this.activeWorkshopCount = workshops.filter(workshop => workshop.status === 'ACTIVE').length;
        this.totalWorkshopCapacity = workshops.filter(workshop => workshop.status === 'ACTIVE').reduce((total, workshop) => total + workshop.capacity, 0);
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }
}
