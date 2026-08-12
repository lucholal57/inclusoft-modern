import { Routes } from '@angular/router';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { StudentsComponent } from './features/students/students.component';
import { LoginComponent } from './features/login/login.component';
import { UsersComponent } from './features/users/users.component';
import { ChangePasswordComponent } from './features/change-password/change-password.component';
import { WorkshopsComponent } from './features/workshops/workshops.component';
import { WorkshopDetailComponent } from './features/workshop-detail/workshop-detail.component';
import { StaffComponent } from './features/staff/staff.component';
import { StudentFileComponent } from './features/student-file/student-file.component';
import { StudentHealthComponent } from './features/student-health/student-health.component';
import { StudentAuthorizationsComponent } from './features/student-authorizations/student-authorizations.component';
import { AttendanceComponent } from './features/attendance/attendance.component';
import { authGuard, passwordChangeGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent, title: 'Ingresar | INCLUsoft' },
  { path: 'cambiar-contrasena', component: ChangePasswordComponent, canActivate: [passwordChangeGuard], title: 'Cambiar contraseña | INCLUsoft' },
  { path: '', component: DashboardComponent, canActivate: [authGuard], title: 'Resumen | INCLUsoft' },
  { path: 'alumnos', component: StudentsComponent, canActivate: [authGuard], title: 'Alumnos | INCLUsoft' },
  { path: 'alumnos/:id/ficha', component: StudentFileComponent, canActivate: [authGuard], title: 'Ficha del alumno | INCLUsoft' },
  { path: 'alumnos/:id/salud', component: StudentHealthComponent, canActivate: [authGuard], title: 'Salud y apoyos | INCLUsoft' },
  { path: 'alumnos/:id/autorizaciones', component: StudentAuthorizationsComponent, canActivate: [authGuard], title: 'Autorizaciones | INCLUsoft' },
  { path: 'asistencia', component: AttendanceComponent, canActivate: [authGuard], title: 'Asistencia | INCLUsoft' },
  { path: 'usuarios', component: UsersComponent, canActivate: [authGuard], title: 'Usuarios | INCLUsoft' },
  { path: 'personal', component: StaffComponent, canActivate: [authGuard], title: 'Personal | INCLUsoft' },
  { path: 'talleres', component: WorkshopsComponent, canActivate: [authGuard], title: 'Talleres | INCLUsoft' },
  { path: 'talleres/:id', component: WorkshopDetailComponent, canActivate: [authGuard], title: 'Taller | INCLUsoft' }
];


