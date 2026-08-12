CREATE TABLE workshops (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL UNIQUE,
    description VARCHAR(500),
    capacity INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_workshops_capacity CHECK (capacity > 0),
    CONSTRAINT ck_workshops_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE TABLE workshop_schedules (
    id UUID PRIMARY KEY,
    workshop_id UUID NOT NULL REFERENCES workshops(id) ON DELETE CASCADE,
    day_of_week VARCHAR(12) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    location VARCHAR(120),
    CONSTRAINT ck_workshop_schedule_time CHECK (end_time > start_time)
);

CREATE TABLE workshop_staff_members (
    workshop_id UUID NOT NULL REFERENCES workshops(id) ON DELETE CASCADE,
    staff_member_id UUID NOT NULL REFERENCES staff_members(id),
    PRIMARY KEY (workshop_id, staff_member_id)
);

CREATE TABLE workshop_students (
    workshop_id UUID NOT NULL REFERENCES workshops(id) ON DELETE CASCADE,
    student_id UUID NOT NULL REFERENCES students(id),
    PRIMARY KEY (workshop_id, student_id)
);

CREATE INDEX idx_workshops_name ON workshops (name);
CREATE INDEX idx_workshop_schedules_workshop ON workshop_schedules (workshop_id);
CREATE INDEX idx_workshop_staff_members_staff ON workshop_staff_members (staff_member_id);
CREATE INDEX idx_workshop_students_student ON workshop_students (student_id);


