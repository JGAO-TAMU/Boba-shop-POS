BEGIN;

INSERT INTO Employee (name, accessLevel, clockIn, clockOut)
VALUES ('John Doe', 1, NOW(), NULL),
VALUES ('Joe Schmo', 0, NOW(), NULL);
COMMIT;
