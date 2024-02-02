DROP VIEW IF EXISTS v_stickers;

CREATE VIEW v_stickers AS

SELECT s._id fileid, a.message_id msgid, a.sticker_emoji FROM attachment a
LEFT JOIN sticker s ON a.sticker_pack_id = s.pack_id AND a.sticker_pack_key = s.pack_key AND a.sticker_id = s.sticker_id
WHERE a.sticker_pack_id NOT NULL;