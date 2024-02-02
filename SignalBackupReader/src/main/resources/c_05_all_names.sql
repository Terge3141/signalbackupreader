DROP VIEW IF EXISTS v_all_names;

CREATE VIEW v_all_names AS

SELECT recipient_id, title 'full_name' FROM groups
UNION
SELECT _id, COALESCE(system_joined_name, profile_joined_name, e164) FROM recipient WHERE group_id IS NULL AND _id != 1
UNION
SELECT 1, '@@MYSELF@@';