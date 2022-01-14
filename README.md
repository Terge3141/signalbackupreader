# SignalBackupReader

## What is Signal Backup Reader?
A very simple api written in java that allows you to read Signal Messenger backup files.

## Compile
First, clone the repository using git (recommended):
```bash
git clone https://github.com/Terge3141/signalbackupreader.git
```

Make sure that *Java 11* and *maven* is installed

Go to the SignalBackupReader directory and build package
```bash
cd SignalBackupReader
mvn package
```

## Dump the database and stickers
```java
Path backupFilePath = Paths.get("backupFilePath"));
Path passphrasePath = Paths.get("passphrasePath");
Path outputDir = Paths.get("outputDir");
DatabaseAndBlobDumper dumper = new DatabaseAndBlobDumper(backupFilePath, passphrasePath, outputDir);
dumper.run();
```

## Run the program to dump database and blobs
```bash
java -cp target/SignalBackupReader-0.0.1-SNAPSHOT-jar-with-dependencies.jar signalbackupreader.Program <backuppath> <passphrasepath> <outputdir>
```
