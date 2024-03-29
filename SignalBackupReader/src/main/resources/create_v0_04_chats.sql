DROP VIEW IF EXISTS v_chats;

CREATE VIEW v_chats AS

SELECT m.msgid, m.date, n1.full_name 'sender', n2.full_name 'chatname', m.text, m.type
FROM v_all_messages m
join thread t on t._id=m.thread_id
join v_all_names n1 on n1.recipient_id=m.sender
join v_all_names n2 on n2.recipient_id=t.thread_recipient_id;
