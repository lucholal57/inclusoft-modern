export type AttendanceStatus = 'PRESENT' | 'LATE' | 'ABSENT_JUSTIFIED' | 'ABSENT_UNJUSTIFIED';
export type AttendanceParticipantType = 'STUDENT' | 'STAFF';
export interface AttendanceEntry { participantType: AttendanceParticipantType; participantId: string; participantName: string; status: AttendanceStatus; observation: string | null; }
export interface AttendanceSession { workshopId: string; workshopName: string; date: string; saved: boolean; notes: string | null; entries: AttendanceEntry[]; }
export interface AttendanceRequest { notes: string | null; entries: { participantType: AttendanceParticipantType; participantId: string; status: AttendanceStatus; observation: string | null; }[]; }
