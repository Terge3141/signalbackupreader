create view v_sms_corrected as

/* Messages sent by me have the receiver as address. In this case set the address to -1 */
select _id 'msgid', thread_id, date, -1 'sender', body 'text', 'sms' 'type'
	from sms where (type is 10485780 or type is 10485783) and date_server = -1
/* add all messages sent by others */
union
select _id 'msgid', thread_id, date, address 'sender', body 'text', 'sms' 'type'
	from sms where (type is 10485780 or type is 10485783) and date_server != -1;
