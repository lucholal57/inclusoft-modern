export interface WorkshopSchedule {
  dayOfWeek: string;
  startTime: string;
  endTime: string;
  location?: string | null;
}

export interface Workshop {
  id: string;
  name: string;
  description: string | null;
  capacity: number;
  status: 'ACTIVE' | 'INACTIVE';
  teamMemberCount: number;
  studentCount: number;
  schedules: WorkshopSchedule[];
}

export interface WorkshopTeamMember { id: string; name: string; profile: string; }
export interface WorkshopPerson { id: string; name: string; }
export interface WorkshopDetail extends Omit<Workshop, 'teamMemberCount' | 'studentCount'> {
  teamMembers: WorkshopTeamMember[];
  students: WorkshopPerson[];
}

export interface CreateWorkshop {
  name: string;
  description?: string | null;
  capacity: number;
  schedules: WorkshopSchedule[];
}
