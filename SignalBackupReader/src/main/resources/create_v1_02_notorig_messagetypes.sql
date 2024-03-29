DROP TABLE IF EXISTS notorig_messagetypes;

CREATE TABLE notorig_messagetypes ("name" TEXT, "val" INTEGER);

INSERT INTO notorig_messagetypes(name, val) VALUES("NONE", 0x0);
INSERT INTO notorig_messagetypes(name, val) VALUES("TOTAL_MASK", 0xFFFFFFFFF);
INSERT INTO notorig_messagetypes(name, val) VALUES("BASE_TYPE_MASK", 0x1F);
INSERT INTO notorig_messagetypes(name, val) VALUES("INCOMING_AUDIO_CALL_TYPE", 1);
INSERT INTO notorig_messagetypes(name, val) VALUES("OUTGOING_AUDIO_CALL_TYPE", 2);
INSERT INTO notorig_messagetypes(name, val) VALUES("MISSED_AUDIO_CALL_TYPE", 3);
INSERT INTO notorig_messagetypes(name, val) VALUES("JOINED_TYPE", 4);
INSERT INTO notorig_messagetypes(name, val) VALUES("UNSUPPORTED_MESSAGE_TYPE", 5);
INSERT INTO notorig_messagetypes(name, val) VALUES("INVALID_MESSAGE_TYPE", 6);
INSERT INTO notorig_messagetypes(name, val) VALUES("PROFILE_CHANGE_TYPE", 7);
INSERT INTO notorig_messagetypes(name, val) VALUES("MISSED_VIDEO_CALL_TYPE", 8);
INSERT INTO notorig_messagetypes(name, val) VALUES("GV1_MIGRATION_TYPE", 9);
INSERT INTO notorig_messagetypes(name, val) VALUES("INCOMING_VIDEO_CALL_TYPE", 10);
INSERT INTO notorig_messagetypes(name, val) VALUES("OUTGOING_VIDEO_CALL_TYPE", 11);
INSERT INTO notorig_messagetypes(name, val) VALUES("GROUP_CALL_TYPE", 12);
INSERT INTO notorig_messagetypes(name, val) VALUES("BAD_DECRYPT_TYPE", 13);
INSERT INTO notorig_messagetypes(name, val) VALUES("CHANGE_NUMBER_TYPE", 14);
INSERT INTO notorig_messagetypes(name, val) VALUES("BOOST_REQUEST_TYPE", 15);
INSERT INTO notorig_messagetypes(name, val) VALUES("THREAD_MERGE_TYPE", 16);
INSERT INTO notorig_messagetypes(name, val) VALUES("SMS_EXPORT_TYPE", 17);
INSERT INTO notorig_messagetypes(name, val) VALUES("SESSION_SWITCHOVER_TYPE", 18);
INSERT INTO notorig_messagetypes(name, val) VALUES("BASE_INBOX_TYPE", 20);
INSERT INTO notorig_messagetypes(name, val) VALUES("BASE_OUTBOX_TYPE", 21);
INSERT INTO notorig_messagetypes(name, val) VALUES("BASE_SENDING_TYPE", 22);
INSERT INTO notorig_messagetypes(name, val) VALUES("BASE_SENT_TYPE", 23);
INSERT INTO notorig_messagetypes(name, val) VALUES("BASE_SENT_FAILED_TYPE", 24);
INSERT INTO notorig_messagetypes(name, val) VALUES("BASE_PENDING_SECURE_SMS_FALLBACK", 25);
INSERT INTO notorig_messagetypes(name, val) VALUES("BASE_PENDING_INSECURE_SMS_FALLBACK", 26);
INSERT INTO notorig_messagetypes(name, val) VALUES("BASE_DRAFT_TYPE", 27);
INSERT INTO notorig_messagetypes(name, val) VALUES("MESSAGE_ATTRIBUTE_MASK", 0xE0);
INSERT INTO notorig_messagetypes(name, val) VALUES("MESSAGE_RATE_LIMITED_BIT", 0x80);
INSERT INTO notorig_messagetypes(name, val) VALUES("MESSAGE_FORCE_SMS_BIT", 0x40);
INSERT INTO notorig_messagetypes(name, val) VALUES("KEY_EXCHANGE_MASK", 0xFF00);
INSERT INTO notorig_messagetypes(name, val) VALUES("KEY_EXCHANGE_BIT", 0x8000);
INSERT INTO notorig_messagetypes(name, val) VALUES("KEY_EXCHANGE_IDENTITY_VERIFIED_BIT", 0x4000);
INSERT INTO notorig_messagetypes(name, val) VALUES("KEY_EXCHANGE_IDENTITY_DEFAULT_BIT", 0x2000);
INSERT INTO notorig_messagetypes(name, val) VALUES("KEY_EXCHANGE_INVALID_VERSION_BIT", 0x800);
INSERT INTO notorig_messagetypes(name, val) VALUES("KEY_EXCHANGE_BUNDLE_BIT", 0x400);
INSERT INTO notorig_messagetypes(name, val) VALUES("KEY_EXCHANGE_IDENTITY_UPDATE_BIT", 0x200);
INSERT INTO notorig_messagetypes(name, val) VALUES("SECURE_MESSAGE_BIT", 0x800000);
INSERT INTO notorig_messagetypes(name, val) VALUES("END_SESSION_BIT", 0x400000);
INSERT INTO notorig_messagetypes(name, val) VALUES("PUSH_MESSAGE_BIT", 0x200000);
INSERT INTO notorig_messagetypes(name, val) VALUES("GROUP_UPDATE_BIT", 0x10000);
INSERT INTO notorig_messagetypes(name, val) VALUES("GROUP_LEAVE_BIT", 0x20000);
INSERT INTO notorig_messagetypes(name, val) VALUES("EXPIRATION_TIMER_UPDATE_BIT", 0x40000);
INSERT INTO notorig_messagetypes(name, val) VALUES("GROUP_V2_BIT", 0x80000);
INSERT INTO notorig_messagetypes(name, val) VALUES("GROUP_V2_LEAVE_BITS", 0xE0000);
INSERT INTO notorig_messagetypes(name, val) VALUES("ENCRYPTION_MASK", 0xFF000000);
INSERT INTO notorig_messagetypes(name, val) VALUES("ENCRYPTION_REMOTE_BIT", 0x20000000);
INSERT INTO notorig_messagetypes(name, val) VALUES("ENCRYPTION_REMOTE_FAILED_BIT", 0x10000000);
INSERT INTO notorig_messagetypes(name, val) VALUES("ENCRYPTION_REMOTE_NO_SESSION_BIT", 0x08000000);
INSERT INTO notorig_messagetypes(name, val) VALUES("ENCRYPTION_REMOTE_DUPLICATE_BIT", 0x04000000);
INSERT INTO notorig_messagetypes(name, val) VALUES("ENCRYPTION_REMOTE_LEGACY_BIT", 0x02000000);
INSERT INTO notorig_messagetypes(name, val) VALUES("SPECIAL_TYPES_MASK", 0xF00000000);
INSERT INTO notorig_messagetypes(name, val) VALUES("SPECIAL_TYPE_STORY_REACTION", 0x100000000);
INSERT INTO notorig_messagetypes(name, val) VALUES("SPECIAL_TYPE_GIFT_BADGE", 0x200000000);
INSERT INTO notorig_messagetypes(name, val) VALUES("SPECIAL_TYPE_PAYMENTS_NOTIFICATION", 0x300000000);
INSERT INTO notorig_messagetypes(name, val) VALUES("SPECIAL_TYPE_PAYMENTS_ACTIVATE_REQUEST", 0x400000000);
INSERT INTO notorig_messagetypes(name, val) VALUES("SPECIAL_TYPE_PAYMENTS_ACTIVATED", 0x800000000);
INSERT INTO notorig_messagetypes(name, val) VALUES("IGNORABLE_TYPESMASK_WHEN_COUNTING", 0x404200);