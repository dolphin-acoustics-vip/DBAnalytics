package src.storage_systems;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import src.CreateRandomData;

public class PopulateSystem {

    private int blobSize, insertions;
    private Connection speedsOfSQLConnection, speedsOfFileOutpuConnection;

    public PopulateSystem(int i, int bS, File storageSizes) throws SQLException {
        insertions = i;
        blobSize = bS;

        // Declaring SQL connections, and turning off autocommit.
        speedsOfFileOutpuConnection = DriverManager.getConnection("jdbc:sqlite:speedsOfFileOutputDatabase.db");
        speedsOfFileOutpuConnection.setAutoCommit(false);

        speedsOfSQLConnection = DriverManager.getConnection("jdbc:sqlite:speedsOfSQLDatabase.db");
        speedsOfSQLConnection.setAutoCommit(false);

        SQLiteStorageSystem sql = new SQLiteStorageSystem("SQLiteStorageSystem");

        Connection SQLDatabaseConn = DriverManager.getConnection("jdbc:sqlite:SQLiteStorageSystem.db");
        SQLDatabaseConn.setAutoCommit(false);

        FileOutputStreamStorage fos = new FileOutputStreamStorage();

        // Inserting rows with the same random data.
        for (int insertion = 0; insertion < insertions; insertion++) {
            CreateRandomData randomDataPoint = new CreateRandomData(blobSize);

            // Get time of this process.
            long startSQL = System.nanoTime();
            sql.store(randomDataPoint, SQLDatabaseConn);
            long endSQL = System.nanoTime();
            long durationSQL = Math.subtractExact(endSQL, startSQL);

            insertSpeed(speedsOfSQLConnection, "SQL", "Insertion", durationSQL);

            // Get time of this process.
            long startFOS = System.nanoTime();
            fos.store(randomDataPoint);
            long endFOS = System.nanoTime();
            long durationFOS = endFOS - startFOS;

            insertSpeed(speedsOfFileOutpuConnection, "FOS", "Insertion", durationFOS);
        }

        speedsOfFileOutpuConnection.commit();
        speedsOfFileOutpuConnection.close();

        speedsOfSQLConnection.commit();
        speedsOfSQLConnection.close();

        SQLDatabaseConn.commit();
        SQLDatabaseConn.close();

        FileWriter fw;

        // Closing all connections and storage systems.
        sql.closeStorage();
        System.out.println("SQL Size = " + sql.getFileSize());
        fos.closeStorage();
        System.out.println("FOS Size = " + fos.getFileSize());

        try {
            fw = new FileWriter(storageSizes, true);
            fw.write("Insertions = " + insertions + "  Blob Size = " + blobSize);
            fw.write("SQL Size = " + sql.getFileSize());
            fw.write("  FOS Size = " + fos.getFileSize());
            fw.write(System.lineSeparator());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void insertSpeed(Connection speedConn, String dbType, String operation, long d) {
        try {
            String insertIntoSpeeds = "INSERT INTO speeds (type_of_db, type_of_statement, blobSize, numberOfInsertions, duration) VALUES (?, ?, ?, ?, ?)";

            PreparedStatement speedOfInsert = speedConn.prepareStatement(insertIntoSpeeds);

            // Setting parameter values.
            speedOfInsert.setString(1, dbType);
            speedOfInsert.setString(2, operation);
            speedOfInsert.setInt(3, blobSize);
            speedOfInsert.setInt(4, insertions);
            speedOfInsert.setString(5, String.valueOf(Math.floorDiv(d, 100)));

            speedOfInsert.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
