DROP VIEW IF EXISTS v_attachments;

CREATE VIEW v_attachments AS

SELECT _id attachmentid, message_id msgid, content_type content_type FROM attachment;