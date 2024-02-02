DROP VIEW IF EXISTS v_stickers;

CREATE VIEW v_stickers AS

SELECT s._id fileid, p.mid msgid, p.sticker_emoji FROM part p 
LEFT JOIN sticker s ON p.sticker_pack_id = s.pack_id AND p.sticker_pack_key = s.pack_key AND p.sticker_id = s.sticker_id
WHERE p.sticker_pack_id NOT NULL;
