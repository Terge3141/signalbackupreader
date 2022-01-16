create view v_mms_corrected as

/* Messages sent by me have the receiver as address. In this case set the address to -1 */
select _id 'msgid', thread_id, date, -1 'sender', body 'text', 'mms' 'type'
	from mms where date_server = -1
/* add all messages sent by others */
union
select _id 'msgid', thread_id, date, address 'sender', body 'text', 'mms' 'type'
	from mms where date_server != -1;
