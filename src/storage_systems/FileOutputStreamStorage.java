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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.text.DateFormatter;

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
            // Storing blocks of data and storing the speeds in the relevant database
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

                // Getting the time it takes to insert one block of 'waveform' data into the file.
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime start_time = LocalDateTime.now();
                store(data);
                LocalDateTime end_time = LocalDateTime.now();
                Duration d = Duration.between(start_time, end_time);


                String insertIntoSpeeds = "INSERT INTO speeds (type_of_db, start_time, end_time, type_of_statement, duration, blobSize) VALUES (?, ?, ?, ?, ?, ?)";

                PreparedStatement speedOfInsert = conn.prepareStatement(insertIntoSpeeds);
                speedOfInsert.setString(1, "FileOutputStream");
                speedOfInsert.setString(2, String.valueOf(df.format(start_time)));
                speedOfInsert.setString(3, String.valueOf(df.format(end_time)));
                speedOfInsert.setString(4, "Inserting");
                speedOfInsert.setString(5, String.valueOf(d));
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
