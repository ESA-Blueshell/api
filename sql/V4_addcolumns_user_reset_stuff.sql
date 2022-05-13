ALTER TABLE users ADD COLUMN resetKey varchar(255);
ALTER TABLE users ADD COLUMN resetKeyValidUntil datetime;
ALTER TABLE users ADD COLUMN resetType varchar(255);
