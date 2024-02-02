--https://github.com/signalapp/Signal-Android/blob/fa937f9f431f489d26e8c84a41681d1b2a04be03/app/src/main/java/org/thoughtcrime/securesms/database/MessageTypes.java
DROP VIEW IF EXISTS v_masks_bits;

CREATE VIEW v_masks_bits AS

SELECT type, count
, type & 0xF00000000 SPECIAL_TYPES_MASK
, type & 0xFF000000 ENCRYPTION_MASK
, type & 0xF0000 GROUP_MASK
, type & 0xFF00 KEY_EXCHANGE_MASK
, type & 0xE0 MESSAGE_ATTRIBUTES
, type & 0x1F BASE_TYPE
FROM v_types;