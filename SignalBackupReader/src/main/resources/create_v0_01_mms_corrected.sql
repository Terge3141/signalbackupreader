DROP VIEW IF EXISTS v_mms_corrected;

CREATE VIEW v_mms_corrected AS

/* Messages sent by me have the receiver as address. In this case set the address to -1 */
SELECT _id 'msgid', thread_id, date, -1 'sender', body 'text', 'media' 'type'
	FROM mms WHERE date_server = -1
/* add all messages sent by others */
UNION
SELECT _id 'msgid', thread_id, date, address 'sender', body 'text', 'media' 'type'
	FROM mms WHERE date_server != -1;
