module signalbackupreader {
	exports org.thoughtcrime.securesms.backup.proto;
	exports signalbackupreader;
	exports signalbackupreader.entry;

	requires com.google.protobuf;
	requires at.favre.lib.hkdf;
	requires java.sql;
	requires org.apache.logging.log4j;
}
