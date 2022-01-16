# SignalBackupReader

## What is Signal Backup Reader?
A very simple api written in java that allows you to read Signal Messenger (https://signal.org/) backup files from the android app.

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

// Create extra sql views
dumper.setCreateExtraSqlViews(true);

dumper.run();
```

## Extra views
As the Signal Messenger database structure is quite complicated, additional views can be created. The view v_chats contains all messages with senders and chatnames:
* msgid (either from mms or sms table)
* date
* sender (Full name)
* chatname (Either recipient name or name of the group)
* text
* type (either mms or sms)
* thread_recipient_id (id in table _threads_)
* senderid (id of _sender_)

The corresponding attachments (e.g. images and pictures) for the mms message can be found in the _part_ table (v_chats.msgid==part.mid)

## Run the program to dump database and blobs
```bash
java -cp target/SignalBackupReader-0.0.1-SNAPSHOT-jar-with-dependencies.jar signalbackupreader.Program <backuppath> <passphrasepath> <outputdir>
```
* backuppath: Path to the android signal messenger file, normally has the name signal-YYYY-MM-DD-HH-MM-SS.backup
* passphrasepath: Pass to the signal passphrase file. Should contain the 30 digits, spaces and new lines are ignored.
* outputdir: the outputdir were the sql data base and the blobs are written to.
