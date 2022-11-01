package src.storage_systems;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import src.CreateRandomData;
import src.CreateSQLiteDatabase;

public class SQLiteStorageSystem implements IStorageSystem {

    private String databaseName, databaseURL, speedsURL;
    private int blobSize;
    private Connection storageConn, speedConn;

    /**
     * Constrctor for making a whole storage system.
     * Only need one SQLite database, and put files into there.
     */
    public SQLiteStorageSystem(String dbName, int rows, int bSize) {
        speedsURL = "jdbc:sqlite:speedsOfSQLDatabase.db";

        blobSize = bSize;
        databaseName = dbName;
        databaseURL = "jdbc:sqlite:" + databaseName + ".db";
        prepareStorage();
        populate(rows);
        closeStorage();
    }

    /**
     * Creates a new SQL database file, and then creates a SQLite connection to the
     * database file created.
     * 
     */
    @Override
    public void prepareStorage() {
        File script = new File("scripts/randomData.txt");
        new CreateSQLiteDatabase(databaseName, script);
    }

    /**
     * This will populate the SQLite database with however many data rows.
     */
    public void populate(int rows) {
        try {
            // Making two connections, one to the storage database itself, and one for the
            // database containing the insert speeds of the different callings of the store
            // method.
            storageConn = DriverManager.getConnection(databaseURL);
            speedConn = DriverManager.getConnection(speedsURL);
            speedConn.setAutoCommit(false);
            storageConn.setAutoCommit(false);

            for (int i = 0; i < rows; i++) {
                reportAnalysis();
            }

            storageConn.commit();
            storageConn.close();
            speedConn.commit();
            speedConn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will store one data file as a table in the SQLDatabase file.
     * 
     * @param data
     */
    @Override
    public void store(byte[] data) {
        try {
            // Put in the different values from the random data point object.
            CreateRandomData randomDataPoint = new CreateRandomData(blobSize);
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
    @Override
    public void closeStorage() {
        File database = new File(databaseName + ".db");
        if (database.delete()) {
        } else {
            System.out.println("Clean up failed.");
        }
    }

    /**
     * This method will give an analysis of the read-write times of different data
     * files.
     */
    @Override
    public void reportAnalysis() {
        try {
            // Getting the time it takes to insert one row into the SQLite database.
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime start_time = LocalDateTime.now();
            store(null);
            LocalDateTime end_time = LocalDateTime.now();
            Duration d = Duration.between(start_time, end_time);

            String insertIntoSpeeds = "INSERT INTO speeds (type_of_db, start_time, end_time, type_of_statement, duration, blobSize) VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement speedOfInsert = speedConn.prepareStatement(insertIntoSpeeds);
            speedOfInsert.setString(1, "SQLite");
            speedOfInsert.setString(2, String.valueOf(df.format(start_time)));
            speedOfInsert.setString(3, String.valueOf(df.format(end_time)));
            speedOfInsert.setString(4, "Inserting");
            speedOfInsert.setString(5, String.valueOf(d));
            speedOfInsert.setInt(6, blobSize);

            speedOfInsert.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}