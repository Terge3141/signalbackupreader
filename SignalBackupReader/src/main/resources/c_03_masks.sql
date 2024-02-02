DROP VIEW IF EXISTS v_masks;

CREATE VIEW v_masks AS

SELECT 
mb.type,
mb.count,
mt1.name SPECIAL_TYPES_MASK, 
mt2.name ENCRYPTION_MASK,
mb.GROUP_MASK GROUP_MASK,
mt4.name KEY_EXCHANGE_MASK,
mt5.name MESSAGE_ATTRIBUTES,
mt6.name BASE_TYPE
FROM v_masks_bits mb
LEFT JOIN notorig_messagetypes mt1 ON mb.SPECIAL_TYPES_MASK=mt1.val
LEFT JOIN notorig_messagetypes mt2 ON mb.ENCRYPTION_MASK=mt2.val
LEFT JOIN notorig_messagetypes mt4 ON mb.KEY_EXCHANGE_MASK=mt4.val
LEFT JOIN notorig_messagetypes mt5 ON mb.MESSAGE_ATTRIBUTES=mt5.val
LEFT JOIN notorig_messagetypes mt6 ON mb.BASE_TYPE=mt6.val;