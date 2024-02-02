DROP VIEW IF EXISTS v_all_messages;

CREATE VIEW v_all_messages AS

SELECT msgid, thread_id, date, sender, text, type FROM v_sms_corrected
UNION
SELECT msgid, thread_id, date, sender, text, type FROM v_mms_corrected;
