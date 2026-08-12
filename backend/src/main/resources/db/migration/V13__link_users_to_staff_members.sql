ALTER TABLE app_users ADD COLUMN staff_member_id UUID UNIQUE REFERENCES staff_members(id);
