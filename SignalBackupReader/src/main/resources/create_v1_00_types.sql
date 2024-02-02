DROP VIEW IF EXISTS v_types;

CREATE VIEW v_types as
SELECT type, COUNT(type) count FROM message GROUP BY type;