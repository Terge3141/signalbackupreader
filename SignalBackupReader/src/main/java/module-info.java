module signalbackupreader {
	exports org.thoughtcrime.securesms.backup;
	exports signalbackupreader;
	exports signalbackupreader.entry;

	requires com.google.protobuf;
	requires hkdf;
	requires java.sql;
	requires transitive org.apache.logging.log4j;
}