CREATE TABLE student_health_records (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL UNIQUE REFERENCES students(id) ON DELETE CASCADE,
    medical_references VARCHAR(1000),
    medications VARCHAR(1000),
    allergies VARCHAR(1000),
    health_insurance VARCHAR(160),
    treating_professionals VARCHAR(1000),
    support_guidelines VARCHAR(2000),
    observations VARCHAR(2000)
);
