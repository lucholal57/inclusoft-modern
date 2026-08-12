CREATE TABLE staff_members (
    id UUID PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    profile VARCHAR(30) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_staff_members_profile CHECK (profile IN ('TEACHER', 'THERAPEUTIC_SUPPORT', 'ASSISTANT', 'OTHER'))
);

CREATE INDEX idx_staff_members_full_name ON staff_members (full_name);
