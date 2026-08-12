export type StaffProfile = 'TEACHER' | 'THERAPEUTIC_SUPPORT' | 'ASSISTANT' | 'OTHER';
export interface StaffMember { id:string; fullName:string; profile:StaffProfile; documentNumber:string|null; phoneNumber:string|null; email:string|null; profession:string|null; active:boolean; }
export type StaffMemberRequest = Omit<StaffMember,'id'|'active'>;