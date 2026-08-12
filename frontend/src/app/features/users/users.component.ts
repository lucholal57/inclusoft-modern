import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ConfirmationService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { DrawerModule } from 'primeng/drawer';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { AuthService } from '../../core/auth/auth.service';
import { AppNavigationComponent } from '../../shared/app-navigation/app-navigation.component';
import { AppUser, UserRole } from './user.model';
import { UsersService } from './users.service';
import { StaffMember } from '../staff/staff.model';
import { StaffService } from '../staff/staff.service';

@Component({
  selector: 'app-users',
  imports: [AppNavigationComponent, ButtonModule, CommonModule, ConfirmDialogModule, DrawerModule, FormsModule, InputTextModule, PasswordModule, ReactiveFormsModule, TableModule, TagModule],
  templateUrl: './users.component.html', styleUrl: './users.component.css'
})
export class UsersComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly usersService = inject(UsersService);
  private readonly confirmationService = inject(ConfirmationService);
  private readonly staffService = inject(StaffService);
  readonly authService = inject(AuthService);
  users: AppUser[] = [];
  filteredUsers: AppUser[] = [];
  staffMembers: StaffMember[] = [];
  loading = true;
  saving = false;
  showForm = false;
  editingUser: AppUser | null = null;
  errorMessage = '';
  searchTerm = '';
  readonly roles: { value: UserRole; label: string; description: string }[] = [
    { value: 'DIRECTOR', label: 'Director/a', description: 'Acceso completo a la gestión institucional.' },
    { value: 'VICE_DIRECTOR', label: 'Vice Director/a', description: 'Gestiona alumnos, talleres y personal; consulta usuarios.' },
    { value: 'TEACHER', label: 'Docente', description: 'Consulta sus talleres, horarios y alumnos asignados.' }
  ];
  readonly form = this.formBuilder.nonNullable.group({
    displayName: ['', [Validators.required, Validators.maxLength(120)]],
    username: ['', [Validators.required, Validators.pattern(/^[a-zA-Z0-9._-]{3,60}$/)]],
    password: ['', [Validators.minLength(8), Validators.maxLength(100)]],
    role: ['DIRECTOR' as UserRole, Validators.required],
    staffMemberId: ['']
  });

  ngOnInit(): void { this.loadUsers(); this.staffService.findAll().subscribe({ next: staff => this.staffMembers = staff.filter(member => member.active) }); }
  get canManageUsers(): boolean { const role = this.authService.session()?.role; return role === 'ADMIN' || role === 'DIRECTOR'; }
  get isEditing(): boolean { return this.editingUser !== null; }
  onSearchChange(): void { this.applySearch(); }
  loadUsers(): void { this.loading = true; this.errorMessage = ''; this.usersService.findAll().subscribe({ next: users => { this.users = users; this.applySearch(); this.loading = false; }, error: error => { this.errorMessage = error.status === 403 ? 'Tu perfil no tiene permiso para administrar usuarios.' : 'No pudimos cargar los usuarios. Verificá que el backend esté iniciado.'; this.loading = false; } }); }
  openForm(): void { this.editingUser = null; this.setPasswordRules(false); this.form.reset({ displayName: '', username: '', password: '', role: 'DIRECTOR', staffMemberId: '' }); this.form.controls.username.enable(); this.errorMessage = ''; this.showForm = true; }
  openEdit(user: AppUser): void { this.editingUser = user; this.setPasswordRules(true); this.form.reset({ displayName: user.displayName, username: user.username, password: '', role: user.role, staffMemberId: user.staffMemberId ?? '' }); this.form.controls.username.disable(); this.errorMessage = ''; this.showForm = true; }
  closeForm(): void { this.showForm = false; this.editingUser = null; this.setPasswordRules(false); this.form.controls.username.enable(); this.form.reset({ displayName: '', username: '', password: '', role: 'DIRECTOR', staffMemberId: '' }); }
  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    if (this.form.controls.role.value === 'TEACHER' && !this.form.controls.staffMemberId.value) { this.form.controls.staffMemberId.markAsTouched(); return; }
    this.saving = true; this.errorMessage = '';
    if (this.editingUser) {
      const value = this.form.getRawValue();
      this.usersService.update(this.editingUser.username, { displayName: value.displayName, role: value.role, newPassword: value.password || null, staffMemberId: value.staffMemberId || null }).subscribe({ next: () => { this.saving = false; this.closeForm(); this.loadUsers(); }, error: error => { this.saving = false; this.errorMessage = error.error?.message ?? 'No pudimos actualizar el usuario.'; } });
      return;
    }
    const value = this.form.getRawValue(); this.usersService.create({ ...value, staffMemberId: value.staffMemberId || null }).subscribe({ next: () => { this.saving = false; this.closeForm(); this.loadUsers(); }, error: error => { this.saving = false; this.errorMessage = error.error?.message ?? 'No pudimos crear el usuario. Revisá los datos e intentá nuevamente.'; } });
  }
  private setPasswordRules(editing: boolean): void { this.form.controls.password.setValidators(editing ? [Validators.minLength(8), Validators.maxLength(100)] : [Validators.required, Validators.minLength(8), Validators.maxLength(100)]); this.form.controls.password.updateValueAndValidity(); }
  confirmStatus(user: AppUser): void {
    const deactivate = user.enabled;
    this.confirmationService.confirm({ header: deactivate ? 'Deshabilitar usuario' : 'Reactivar usuario', message: deactivate ? `¿Querés deshabilitar el acceso de ${user.displayName}? Podrás reactivarlo cuando sea necesario.` : `¿Querés reactivar el acceso de ${user.displayName}?`, icon: deactivate ? 'pi pi-exclamation-triangle' : 'pi pi-check-circle', acceptLabel: deactivate ? 'Deshabilitar' : 'Reactivar', rejectLabel: 'Cancelar', acceptButtonStyleClass: deactivate ? 'p-button-danger' : '', accept: () => (deactivate ? this.usersService.deactivate(user.username) : this.usersService.activate(user.username)).subscribe({ next: () => this.loadUsers(), error: error => this.errorMessage = error.error?.message ?? 'No pudimos actualizar el estado del usuario.' }) });
  }
  roleLabel(role: UserRole): string { return role === 'VICE_DIRECTOR' ? 'Vice Dirección' : role === 'DIRECTOR' ? 'Dirección' : role === 'TEACHER' ? 'Docente' : 'Administración'; }
  roleSeverity(role: UserRole): 'info' | 'secondary' | 'contrast' { return role === 'DIRECTOR' ? 'contrast' : role === 'VICE_DIRECTOR' ? 'info' : 'secondary'; }
  selectedRoleDescription(): string { return this.roles.find(role => role.value === this.form.controls.role.value)?.description ?? ''; }
  private applySearch(): void { const term = this.searchTerm.trim().toLocaleLowerCase(); this.filteredUsers = !term ? this.users : this.users.filter(user => `${user.displayName} ${user.username} ${this.roleLabel(user.role)} ${user.staffMemberName ?? ''}`.toLocaleLowerCase().includes(term)); }
  availableStaffMembers(): StaffMember[] { const linkedToAnotherUser = new Set(this.users.filter(user => user.id !== this.editingUser?.id && user.staffMemberId).map(user => user.staffMemberId)); return this.staffMembers.filter(member => !linkedToAnotherUser.has(member.id)); }
}
