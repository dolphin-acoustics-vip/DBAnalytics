package src.storage_systems;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;

public class AbstractStorageSystem implements IStorageSystem {

    private String speedsURL;

    /**
     * This method will create the storage system.
     */
    @Override
    public void prepareStorage() {
        // TODO Auto-generated method stub

    }

    /**
     * Get the time the method starts and the time it ends
     * - Nano seconds
     * - Clock time - System class says how much time it takes
     * - Thread class - how much CPU the thread uses - could maybe use different
     * threads to read several bits of data into the database
     */
    @Override
    public void store(byte[] data) {
        // TODO Auto-generated method stub

    }

    @Override
    public void closeStorage() {
        // TODO Auto-generated method stub

    }

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
