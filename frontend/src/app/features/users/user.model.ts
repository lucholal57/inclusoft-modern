export type UserRole = 'ADMIN' | 'DIRECTOR' | 'VICE_DIRECTOR' | 'TEACHER';

export interface AppUser {
  id: string;
  username: string;
  displayName: string;
  role: UserRole;
  enabled: boolean;
  mustChangePassword: boolean;
  staffMemberId: string | null;
  staffMemberName: string | null;
}

export interface CreateUserRequest {
  username: string;
  displayName: string;
  password: string;
  role: UserRole;
  staffMemberId?: string | null;
}

export interface UpdateUserRequest {
  displayName: string;
  role: UserRole;
  newPassword?: string | null;
  staffMemberId?: string | null;
}
