create view v_all_messages as

select msgid, thread_id, date, sender, text, type from v_sms_corrected
union
select msgid, thread_id, date, sender, text, type from v_mms_corrected;
