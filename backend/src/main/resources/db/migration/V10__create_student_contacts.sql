CREATE TABLE student_contacts (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    full_name VARCHAR(120) NOT NULL,
    relationship VARCHAR(80) NOT NULL,
    phone_number VARCHAR(30) NOT NULL,
    email VARCHAR(120),
    responsible BOOLEAN NOT NULL DEFAULT FALSE,
    emergency_contact BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_student_contacts_student ON student_contacts(student_id);
