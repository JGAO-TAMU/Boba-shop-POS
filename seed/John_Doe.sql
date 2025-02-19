BEGIN;

INSERT INTO Employee (name, accessLevel, clockIn, clockOut)
VALUES ('John Doe', 1, NOW(), NULL);

COMMIT;
