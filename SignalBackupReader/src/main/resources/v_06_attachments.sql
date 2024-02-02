DROP VIEW IF EXISTS v_attachments;

CREATE VIEW v_attachments AS

SELECT unique_id attachmentid, mid msgid, ct content_type FROM part;
