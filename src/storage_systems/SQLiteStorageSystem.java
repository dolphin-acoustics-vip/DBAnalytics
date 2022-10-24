package src.storage_systems;

import java.io.File;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;

import javax.sql.rowset.serial.SerialBlob;

import src.CreateRandomData;
import src.CreateSQLiteDatabase;

public class SQLiteStorageSystem implements IStorageSystem {

    private String databaseName, databaseURL, speedsURL;

    /**
     * Constrctor for making a whole storage system.
     * Only need one SQLite database, and put files into there.
     */
    public SQLiteStorageSystem(String dbName) {
        speedsURL = "jdbc:sqlite:speedsOfSQLDatabase.db";
        File script = new File("scripts/databaseAnalysis.txt");
        new CreateSQLiteDatabase("speedsOfSQLDatabase", script);

        databaseName = dbName;
        databaseURL = "jdbc:sqlite:" + databaseName + ".db";
        prepareStorage();
        populate(5);
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
        for (int i = 0; i < rows; i++) {
            reportAnalysis();
        }
    }

    /**
     * This method will store one data file as a table in the SQLDatabase file.
     * 
     * @param data
     */
    @Override
    public void store(byte[] data) {

        Connection connection;

        try {
            connection = DriverManager.getConnection(databaseURL);

            // Put in the different values from the random data point object.
            CreateRandomData randomDataPoint = new CreateRandomData(20);
            StringBuilder sb = new StringBuilder();
            sb.append(
                    "INSERT INTO RandomData (time_recorded, ship_id, data, start_time, end_time, duration, channel_recorded) VALUES (?, ?, ?, ?, ?, ?, ?)");
            // time recorded, ship id, waveform data, start time, end time, duration,
            // channel recorded
            PreparedStatement insertDataRowCommand = connection.prepareStatement(sb.toString());

            // Convert bytes[] to blob

            Blob dataBlob = new SerialBlob(randomDataPoint.getWaveformData());

            insertDataRowCommand.setString(1, String.valueOf(randomDataPoint.getDateRecorded())); // time recorded
            insertDataRowCommand.setString(2, String.valueOf(randomDataPoint.getShipName())); // ship id
            insertDataRowCommand.setString(3, String.valueOf(randomDataPoint.getWaveformData())); // waveform data
            insertDataRowCommand.setString(4, String.valueOf(randomDataPoint.getStartRecordTime())); // start time
            insertDataRowCommand.setString(5, String.valueOf(randomDataPoint.getEndRecordTime())); // end time
            insertDataRowCommand.setString(6, String.valueOf(randomDataPoint.getDurationOfWaveformSound())); // duration
            insertDataRowCommand.setString(7, String.valueOf(randomDataPoint.getChannel())); // channel recorded

            insertDataRowCommand.execute();

            connection.close();

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
            System.out.println("Clean up complete.");
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
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(speedsURL);

            Instant s = Instant.now();
            store(null);
            Instant e = Instant.now();

            Duration duration = Duration.between(s, e);

            String insertIntoSpeeds = "INSERT INTO speeds (type_of_db, start_time, end_time, type_of_statement, duration) VALUES (?, ?, ?, ?, ?)";

            PreparedStatement speedOfInsert = conn.prepareStatement(insertIntoSpeeds);
            speedOfInsert.setString(1, "SQLite");
            speedOfInsert.setString(2, String.valueOf(s));
            speedOfInsert.setString(3, String.valueOf(e));
            speedOfInsert.setString(4, "Inserting");
            speedOfInsert.setString(5, String.valueOf(duration));

            speedOfInsert.execute();

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}