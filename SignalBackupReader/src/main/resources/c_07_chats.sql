DROP VIEW IF EXISTS v_chats;

CREATE VIEW v_chats AS

SELECT mm._id msgid, mm.date_sent date, n1.full_name sender
, n2.full_name chatname, mm.body text
, mt.type type
FROM v_messages_and_masks mm

LEFT JOIN thread t ON mm.thread_id = t._id
LEFT JOIN v_all_names n1 ON n1.recipient_id=mm.from_recipient_id
LEFT JOIN v_all_names n2 ON n2.recipient_id=t.recipient_id
LEFT JOIN v_messages_mediatypes mt ON mt.msgid = mm._id

WHERE (mm.GROUP_MASK=0) AND (mm.BASE_TYPE='BASE_INBOX_TYPE' OR mm.BASE_TYPE='BASE_SENT_TYPE');