DELETE FROM contributions
WHERE NOT paid;

ALTER TABLE contributions
    DROP COLUMN paid;
