package src.storage_systems;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;

import src.CreateRandomData;
import src.CreateSQLiteDatabase;

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
public class FileOutputStreamStorage implements IStorageSystem {

    private String speedsURL;
    private int blobSize, blocks;
    private File storage;
    private String fileName;

    public FileOutputStreamStorage(int numberOfBlocks, int sizeOfBlob) {
        blobSize = sizeOfBlob;
        blocks = numberOfBlocks;
        speedsURL = "jdbc:sqlite:speedsOfFileOutputDatabase.db";

        prepareStorage();
        closeStorage();
    }

    @Override
    public void prepareStorage() {
        fileName = "fileStorageSystem.txt";
        storage = new File(fileName);
        try {
            storage.createNewFile();
            reportAnalysis();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void store(byte[] data) {
        try {
            FileOutputStream fStream = new FileOutputStream(fileName, true);
            fStream.write(data);
            fStream.write("/n".getBytes());
            fStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeStorage() {
        storage.delete();
    }

    @Override
    public void reportAnalysis() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(speedsURL);
            conn.setAutoCommit(false);
            for (int i = 0; i < blocks; i++) {

                CreateRandomData rd = new CreateRandomData(blobSize);
                byte[] data = rd.getWaveformData();

                Instant s = Instant.now();
                store(data);
                Instant e = Instant.now();

                Duration duration = Duration.between(s, e);

                String insertIntoSpeeds = "INSERT INTO speeds (type_of_db, start_time, end_time, type_of_statement, duration, blobSize) VALUES (?, ?, ?, ?, ?, ?)";

                PreparedStatement speedOfInsert = conn.prepareStatement(insertIntoSpeeds);
                speedOfInsert.setString(1, "FileOutputStream");
                speedOfInsert.setString(2, String.valueOf(s));
                speedOfInsert.setString(3, String.valueOf(e));
                speedOfInsert.setString(4, "Inserting");
                speedOfInsert.setString(5, String.valueOf(duration));
                speedOfInsert.setInt(6, blobSize);

                speedOfInsert.execute();

            }
            conn.commit();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
