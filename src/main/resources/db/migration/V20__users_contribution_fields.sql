ALTER TABLE users
    DROP COLUMN contribution_paid,
    DROP COLUMN online_signup,
    ADD COLUMN member_type TEXT,
    ADD COLUMN incasso BOOLEAN DEFAULT FALSE;

UPDATE users
SET member_type = 'REGULAR';
