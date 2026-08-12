CREATE TABLE attendance_sessions (
    id UUID PRIMARY KEY,
    workshop_id UUID NOT NULL REFERENCES workshops(id),
    attendance_date DATE NOT NULL,
    notes VARCHAR(1000),
    created_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_attendance_sessions_workshop_date UNIQUE (workshop_id, attendance_date)
);
CREATE TABLE attendance_entries (
    id UUID PRIMARY KEY,
    session_id UUID NOT NULL REFERENCES attendance_sessions(id) ON DELETE CASCADE,
    participant_type VARCHAR(15) NOT NULL,
    participant_id UUID NOT NULL,
    participant_name VARCHAR(160) NOT NULL,
    attendance_status VARCHAR(20) NOT NULL,
    observation VARCHAR(500),
    CONSTRAINT uq_attendance_entries_session_person UNIQUE (session_id, participant_type, participant_id),
    CONSTRAINT ck_attendance_participant_type CHECK (participant_type IN ('STUDENT', 'STAFF')),
    CONSTRAINT ck_attendance_status CHECK (attendance_status IN ('PRESENT', 'LATE', 'ABSENT_JUSTIFIED', 'ABSENT_UNJUSTIFIED'))
);
