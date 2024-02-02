DROP VIEW IF EXISTS v_sms_corrected;

CREATE VIEW v_sms_corrected AS

/* Messages sent by me have the receiver as address. In this case set the address to -1 */
SELECT _id 'msgid', thread_id, date, -1 'sender', body 'text', 'text' 'type'
	FROM sms where (type is 10485780 or type is 10485783) AND date_server = -1
/* add all messages sent by others */
UNION
SELECT _id 'msgid', thread_id, date, address 'sender', body 'text', 'text' 'type'
	FROM sms where (type is 10485780 or type is 10485783) AND date_server != -1;
