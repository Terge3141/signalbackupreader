DROP VIEW IF EXISTS v_all_names;

CREATE VIEW v_all_names AS
SELECT recipient_id, title 'full_name' FROM groups
UNION
SELECT _id, system_display_name FROM recipient WHERE group_id IS NULL
UNION
SELECT -1, '@@MYSELF@@';
