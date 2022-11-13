package src;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class AnalyseData {
    
    // Manually creating the connections to the two databases here.
    private Connection speedsOfSQLConnection, speedsOfFileOutpuConnection;


    // This class will get the data from the SQLite databases and plot it on a graph.
    AnalyseData(int[] numberOfInsertions, int[] blobSizes) throws SQLException {

        // Made connections, will use these to get the relevant data.
        speedsOfFileOutpuConnection = DriverManager.getConnection("jdbc:sqlite:speedsOfFileOutputDatabase.db");
        speedsOfSQLConnection = DriverManager.getConnection("jdbc:sqlite:speedsOfSQLDatabase.db");

        for (int i : blobSizes) {
            for (int j : numberOfInsertions) {
                getTotalTime(j, i);
            }
        }

        speedsOfFileOutpuConnection.close();
        speedsOfSQLConnection.close();
    }

    /**
     * 
     * @param noOfInsertions
     * @param blobSize
     * @throws SQLException
     */
    private void getTotalTime(int noOfInsertions, int blobSize) throws SQLException {

        // Get all the times for a certain blob size and number of insertions.
        String sqlQuery = "SELECT duration FROM speeds WHERE blobSize IS " + blobSize + " AND numberOfInsertions IS " + noOfInsertions + " ORDER BY duration ASC";


        Statement sql = speedsOfSQLConnection.createStatement();
        ResultSet speedsSQL = sql.executeQuery(sqlQuery);

        Statement file = speedsOfFileOutpuConnection.createStatement();
        ResultSet speedsFile = file.executeQuery(sqlQuery);

        // Starting the loop
        float speedSQL = speedsSQL.getFloat("duration");
        float speedFile = speedsFile.getFloat("duration");

        float lowestSQL = speedSQL / 1000000;
        float lowestFile = speedFile / 1000000;

        // Get upper and lower quartiles.
        int quarter = noOfInsertions / 4;
        int lower = quarter;
        int upper = quarter * 3;

        float upperQuartileSQL = 0;
        float upperQuartileFile = 0;

        float lowerQuartileSQL = 0;
        float lowerQuartileFile = 0;

        float totalSQL = 0;
        float totalFile = 0;

        do {
            // Add speed to running total.
            totalSQL = totalSQL + speedSQL;
            totalFile = totalFile + speedFile;

            if (speedsSQL.getRow() == lower) {
                lowerQuartileSQL = speedSQL / 1000000;
            } else if (speedsFile.getRow() == upper) {
                upperQuartileSQL = speedSQL / 1000000;
            }

            if (speedsFile.getRow() == lower) {
                lowerQuartileFile = speedFile / 1000000;
            } else if (speedsFile.getRow() == upper) {
                upperQuartileFile = speedFile / 1000000;
            }

            speedSQL = speedsSQL.getFloat("duration");
            speedFile = speedsFile.getFloat("duration");

            // Write speed to some sort of file.

        } while (speedsSQL.next() && speedsFile.next());

        float highestSQL = speedSQL / 1000000;
        float highestFile = speedFile / 1000000;

        // Divide the running total by number of insertions to get the average time.
        // Also write this to the excel file.
        float averageNanosSQL = totalSQL / noOfInsertions;
        float averageNanosFile = totalFile / noOfInsertions;

        float averageSQL = averageNanosSQL / 1000000;
        float averageFile = averageNanosFile / 1000000;

        // Need to format the average and then write it to excel.

        

    }
    
    
}
