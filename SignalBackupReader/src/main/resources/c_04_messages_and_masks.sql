-- masks and message

DROP VIEW IF EXISTS v_messages_and_masks;

CREATE VIEW v_messages_and_masks AS

SELECT * FROM message msg
LEFT JOIN v_masks mask ON msg.type=mask.type;
