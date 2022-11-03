package src.storage_systems;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import src.CreateRandomData;

public class PopulateSystem {

    private int blobSize, insertions;
    private Connection speedsOfSQLConnection, speedsOfFileOutpuConnection;

    public PopulateSystem(int i, int bS) throws SQLException {
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
            long startSQL = System.currentTimeMillis();
            System.out.println("start = " + startSQL);
            sql.store(randomDataPoint, SQLDatabaseConn);
            long endSQL = System.currentTimeMillis();
            System.out.println("end   = " + endSQL);
            long durationSQL = Math.subtractExact(endSQL, startSQL);

            insertSpeed(speedsOfSQLConnection, "SQL", "Insertion", durationSQL);

            // Get time of this process.
            long startFOS = System.currentTimeMillis();
            fos.store(randomDataPoint);
            long endFOS = System.currentTimeMillis();
            long durationFOS = endFOS - startFOS;

            insertSpeed(speedsOfFileOutpuConnection, "FOS", "Insertion", durationFOS);
        }

        System.out.println("commit time start = " + System.currentTimeMillis());
        speedsOfFileOutpuConnection.commit();
        System.out.println("commit time end = " + System.currentTimeMillis());

        speedsOfFileOutpuConnection.close();

        speedsOfSQLConnection.commit();
        speedsOfSQLConnection.close();

        SQLDatabaseConn.commit();
        SQLDatabaseConn.close();

        // Closing all connections and storage systems.
        sql.closeStorage();
        fos.closeStorage();
    }

    private void insertSpeed(Connection speedConn, String dbType, String operation, long d) {
        try {
            String insertIntoSpeeds = "INSERT INTO speeds (type_of_db, type_of_statement, duration, blobSize, numberOfInsertions) VALUES (?, ?, ?, ?, ?)";

            PreparedStatement speedOfInsert = speedConn.prepareStatement(insertIntoSpeeds);

            // Setting parameter values.
            speedOfInsert.setString(1, "SQLite");
            speedOfInsert.setString(2, "Inserting");
            speedOfInsert.setString(3, String.valueOf(d));
            speedOfInsert.setInt(4, blobSize);
            speedOfInsert.setInt(5, insertions);

            speedOfInsert.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
