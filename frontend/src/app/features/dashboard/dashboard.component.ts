import { CommonModule, DatePipe } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { AuthService } from '../../core/auth/auth.service';
import { AttendanceSession } from '../attendance/attendance.model';
import { AttendanceService } from '../attendance/attendance.service';
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
  private readonly attendanceService = inject(AttendanceService);
  readonly authService = inject(AuthService);
  readonly today = new Date();
  studentCount: number | null = null;
  activeStudentCount: number | null = null;
  activeStaffCount: number | null = null;
  staffCount: number | null = null;
  activeWorkshopCount: number | null = null;
  totalWorkshopCapacity: number | null = null;
  teacherTodayWorkshops: TeacherWorkshop[] = [];
  teacherStudentCount: number | null = null;
  teacherAttendancePending = 0;
  teacherLoadError = false;
  loading = true;

  get canManageInstitute(): boolean { return this.authService.session()?.role !== 'TEACHER'; }
  get canManageUsers(): boolean { const role = this.authService.session()?.role; return role === 'ADMIN' || role === 'DIRECTOR'; }
  get greetingName(): string { return this.authService.session()?.displayName?.split(' ')[0] ?? 'bienvenido'; }

  ngOnInit(): void {
    if (!this.canManageInstitute) { this.loadTeacherSummary(); return; }
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

  get hasTeacherWorkshops(): boolean { return this.teacherTodayWorkshops.length > 0; }

  private loadTeacherSummary(): void {
    const today = this.toApiDate(this.today);
    forkJoin({ students: this.studentsService.findAll(), workshops: this.workshopsService.findAll(), attendance: this.attendanceService.workshops(today) }).subscribe({
      next: ({ students, workshops, attendance }) => {
        this.teacherStudentCount = students.filter(student => student.status === 'ACTIVE').length;
        this.teacherAttendancePending = attendance.filter(session => !session.saved).length;
        this.teacherTodayWorkshops = this.toTeacherWorkshops(attendance, workshops);
        this.loading = false;
      },
      error: () => { this.teacherLoadError = true; this.loading = false; }
    });
  }

  private toTeacherWorkshops(attendance: AttendanceSession[], workshops: { id: string; schedules: { dayOfWeek: string; startTime: string; endTime: string; location?: string | null; }[] }[]): TeacherWorkshop[] {
    const day = this.dayOfWeek(this.today);
    return attendance.map(session => {
      const schedule = workshops.find(workshop => workshop.id === session.workshopId)?.schedules.find(item => item.dayOfWeek === day);
      return { id: session.workshopId, name: session.workshopName, saved: session.saved, schedule: schedule ? `${this.formatTime(schedule.startTime)}–${this.formatTime(schedule.endTime)}` : 'Horario a confirmar', location: schedule?.location ?? null };
    }).sort((first, second) => first.schedule.localeCompare(second.schedule));
  }

  private toApiDate(value: Date): string { const offset = value.getTimezoneOffset() * 60_000; return new Date(value.getTime() - offset).toISOString().slice(0, 10); }
  private dayOfWeek(value: Date): string { return ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'][value.getDay()]; }
  private formatTime(value: string): string { return value.slice(0, 5); }
}

interface TeacherWorkshop { id: string; name: string; saved: boolean; schedule: string; location: string | null; }
