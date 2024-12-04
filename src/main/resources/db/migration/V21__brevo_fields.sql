ALTER TABLE users
    DROP COLUMN in_brevo,
    ADD COLUMN contact_id BIGINT;
