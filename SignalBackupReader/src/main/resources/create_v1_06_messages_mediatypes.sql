DROP VIEW IF EXISTS v_messages_mediatypes;

CREATE VIEW v_messages_mediatypes AS

SELECT m._id msgid, 'text' type FROM message m
LEFT JOIN attachment a ON m._id=a.message_id
WHERE a.message_id IS NULL
UNION
SELECT m._id msg, 'media' type FROM message m
LEFT JOIN attachment a ON m._id=a.message_id
WHERE a.message_id IS NOT NULL;
