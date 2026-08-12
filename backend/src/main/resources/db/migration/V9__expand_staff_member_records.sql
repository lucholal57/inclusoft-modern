ALTER TABLE staff_members ADD COLUMN document_number VARCHAR(20);
ALTER TABLE staff_members ADD COLUMN phone_number VARCHAR(30);
ALTER TABLE staff_members ADD COLUMN email VARCHAR(120);
ALTER TABLE staff_members ADD COLUMN profession VARCHAR(120);
CREATE UNIQUE INDEX uq_staff_members_document_number ON staff_members (document_number);