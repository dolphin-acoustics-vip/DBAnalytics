package src.storage_systems;

import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.rowset.serial.SerialBlob;

import src.CreateRandomData;
import src.CreateSQLiteDatabase;

public class SQLiteStorageSystem implements IStorageSystem {

    private String databaseName, databaseURL;

    /**
     * Constrctor for making a whole storage system.
     * Only need one SQLite database, and put files into there.
     */
    public SQLiteStorageSystem(String dbName) {
        databaseName = dbName;
        databaseURL = "jdbc:sqlite:" + databaseName + ".db";
        // This method will create a new databse file for the recorded data to be
        // written to.
        prepareStorage();
        populate(5);
        // closeStorage();
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

        /**
        // Creating a new database file (.db) with the given name for the whole
        // database, this is not a path (at the moment).
        File database = new File(databaseName + ".db");

        try {
            if (database.createNewFile()) {
                System.out.println("Made file");
            } else if (database.delete()) {
                // This is done to delete any previous instances of the database to create a
                // wholly new one.
                database.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Database file could not be created.");
        }

        // Creating the SQL database
        checkIfDBExists();
        */

    }

    /**
     * This connects to the database and creates a table in the database. It double
     * checks the database file has been created.
     * From CS1003 P3 (My submission)
     * 
     * @param databasePath
     * @throws SQLException
     * @throws IOException
     */
    private void checkIfDBExists() {

        // Check if a database file exists, the database file will be called DVLA.db
        Connection connection = null;

        try {
            // Connect to the Database Management System
            connection = DriverManager.getConnection(databaseURL);

            // Create a table in the database, where each row will be one data file.
            String tableName = "RandomData";
            StringBuilder sb = new StringBuilder();
            sb.append("DROP TABLE IF EXISTS " + tableName + ";");
            Statement checkToDropTable = connection.createStatement();
            checkToDropTable.execute(sb.toString());

            StringBuilder sb2 = new StringBuilder();

            sb2.append("CREATE TABLE " + tableName + " (" +
                    "time_recorded TEXT PRIMARY KEY, " +
                    "ship_id TEXT, " +
                    "data TEXT, " +
                    "start_time TEXT, " +
                    "end_time TEXT, " +
                    "duration TEXT, " +
                    "channel_recorded TEXT" +
                    ");");

            Statement statement = connection.createStatement();
            statement.execute(sb2.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database cannot be connected to");
        } finally {
            // Regardless of whether an exception occurred above or not,
            // make sure we close the connection to the Database Management System
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("Connection cannot be closed.");
                }
            }
        }

    }

    /**
     * This will populate the SQLite database with however many data rows.
     */
    public void populate(int rows) {
        for (int i = 0; i < rows; i++) {
            store(null);
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
            insertDataRowCommand.setString(3, String.valueOf(randomDataPoint.getWaveformData())); // raw data of
                                                                                                  // waveform
            insertDataRowCommand.setString(4, String.valueOf(randomDataPoint.getStartRecordTime())); // start time
            insertDataRowCommand.setString(5, String.valueOf(randomDataPoint.getEndRecordTime())); // end time
            insertDataRowCommand.setString(6, String.valueOf(randomDataPoint.getDurationOfWaveformSound())); // duration
            insertDataRowCommand.setString(7, String.valueOf(randomDataPoint.getChannel())); // channel recorded

            insertDataRowCommand.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        /**
         * Get the write times of each row during this time.
         */

        // Make a table with the name associated with the type of data being stored (the
        // size)

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
            conn = DriverManager.getConnection(databaseURL);

            /**
             * Get the read write times of the database
             */

        } catch (SQLException e) {
            System.out.println("Cannot connect to " + databaseName);
            e.printStackTrace();
        }

    }

}