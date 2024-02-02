drop view if exists v_attachments;

create view v_attachments as

select unique_id attachmentid, mid msgid, ct content_type from part;
