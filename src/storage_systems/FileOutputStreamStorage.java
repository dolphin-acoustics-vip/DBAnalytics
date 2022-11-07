package src.storage_systems;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import src.CreateRandomData;

/**
 * Testing how fast it is to read data that was written directly with
 * FileOutputStream.
 * Technically would be faster as the files will only be storing the raw byte
 * data of the waveform itself - however, it could be more useful to have a
 * SQLite database, where you can quickly get a specific time of recording.
 * 
 * 
 * There would be a lot of blobs to read through, these blobs are the waveform
 * data itself.
 * 
 * blocks of data in one file
 */
public class FileOutputStreamStorage {

    private File storage;
    private String fileName;
    private FileOutputStream fStream;

    public FileOutputStreamStorage() {
        prepareStorage();
    }

    private void prepareStorage() {

        // Creating file for file storage system.
        fileName = "fileStorageSystem.txt";
        storage = new File(fileName);
        try {
            storage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Creating an associated FileOutputStream
        try {
            fStream = new FileOutputStream(fileName, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void store(CreateRandomData randomDataPoint) {
        try {
            fStream.write(convertToByteArray(randomDataPoint.getDateRecorded()));
            fStream.write(convertToByteArray(randomDataPoint.getShipName()));
            fStream.write(randomDataPoint.getWaveformData());
            fStream.write(convertToByteArray(randomDataPoint.getDurationOfWaveformSound()));
            fStream.write(convertToByteArray(randomDataPoint.getChannel()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] convertToByteArray(Object toConvert) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os;
        try {
            os = new ObjectOutputStream(bos);
            os.writeObject(toConvert);
            os.flush();
            return bos.toByteArray();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return null;
    }

    public void closeStorage() {
        try {
            fStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        storage.delete();
    }

}
