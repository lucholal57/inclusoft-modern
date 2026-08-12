export type UserRole = 'ADMIN' | 'DIRECTOR' | 'VICE_DIRECTOR' | 'TEACHER';

export interface AuthSession {
  token: string;
  username: string;
  displayName: string;
  role: UserRole;
  mustChangePassword: boolean;
}