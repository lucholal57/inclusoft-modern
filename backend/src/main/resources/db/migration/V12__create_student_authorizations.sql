CREATE TABLE student_authorizations (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL UNIQUE REFERENCES students(id) ON DELETE CASCADE,
    image_use_authorized BOOLEAN NOT NULL DEFAULT FALSE,
    local_outings_authorized BOOLEAN NOT NULL DEFAULT FALSE,
    medical_emergency_authorized BOOLEAN NOT NULL DEFAULT FALSE,
    data_sharing_authorized BOOLEAN NOT NULL DEFAULT FALSE,
    authorized_by VARCHAR(120),
    authorization_date DATE,
    observations VARCHAR(2000)
);
