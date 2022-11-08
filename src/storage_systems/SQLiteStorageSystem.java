package src.storage_systems;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

import src.CreateRandomData;
import src.CreateSQLiteDatabase;

public class SQLiteStorageSystem {

    private String databaseName;
    private long fileSize;

    /**
     * Constrctor for making a whole storage system.
     * Only need one SQLite database, and put files into there.
     */
    public SQLiteStorageSystem(String dbName) {
        databaseName = dbName;

        prepareStorage();
    }

    /**
     * Creates a new SQL database file, and then creates a SQLite connection to the
     * database file created.
     * 
     */
    public void prepareStorage() {
        File script = new File("scripts/randomData.txt");
        new CreateSQLiteDatabase(databaseName, script);
    }

    /**
     * This method will store one data file as a table in the SQLDatabase file.
     * 
     * @param data
     */
    public void store(CreateRandomData randomDataPoint, Connection storageConn) {
        try {
            // Put in the different values from the random data point object.
            // CreateRandomData randomDataPoint = new CreateRandomData(blobSize);
            StringBuilder sb = new StringBuilder();
            sb.append(
                    "INSERT INTO RandomData (time_recorded, ship_id, data, duration, channel_recorded) VALUES (?, ?, ?, ?, ?)");

            // Using the global connection to the storage database.
            PreparedStatement insertDataRowCommand = storageConn.prepareStatement(sb.toString());

            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            insertDataRowCommand.setString(1, String.valueOf(df.format(randomDataPoint.getDateRecorded()))); // time
                                                                                                             // recorded
            insertDataRowCommand.setString(2, String.valueOf(randomDataPoint.getShipName())); // ship id
            insertDataRowCommand.setBytes(3, randomDataPoint.getWaveformData()); // waveform data
            insertDataRowCommand.setString(4, String.valueOf(randomDataPoint.getDurationOfWaveformSound())); // duration
            insertDataRowCommand.setString(5, String.valueOf(randomDataPoint.getChannel())); // channel recorded

            insertDataRowCommand.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method will read in the different tables to get the read time of
     * different data files, and then delete the table. Finally, the database file
     * itself will be deleted.
     */
    public void closeStorage() {
        File database = new File(databaseName + ".db");
        // Get the size of this database.
        try {
            fileSize = Files.size(Paths.get(database.getAbsolutePath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (database.delete()) {
        } else {
            System.out.println("Clean up failed.");
        }
    }

    public long getFileSize() {
        return fileSize;
    }

}