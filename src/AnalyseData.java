package src;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class AnalyseData {
    
    // Manually creating the connections to the two databases here.
    private Connection speedsOfSQLConnection, speedsOfFileOutpuConnection;
    private Workbook workbook;
    private int sheetNo;


    // This class will get the data from the SQLite databases and plot it on a graph.
    AnalyseData(int[] numberOfInsertions, int[] blobSizes) throws SQLException, IOException {

        // Make connections, will use these to get the relevant data.
        speedsOfFileOutpuConnection = DriverManager.getConnection("jdbc:sqlite:speedsOfFileOutputDatabase.db");
        speedsOfSQLConnection = DriverManager.getConnection("jdbc:sqlite:speedsOfSQLDatabase.db");

        // Make a workbook to write to.
        workbook = new XSSFWorkbook();
        sheetNo = 1;

        for (int i : numberOfInsertions) {
            for (int j : blobSizes) {
                getTotalTime(i, j);
                sheetNo++;
            }
        }

        speedsOfFileOutpuConnection.close();
        speedsOfSQLConnection.close();

        File times = new File("times.xlsx");
        String path = times.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";

        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        workbook.write(outputStream);
        workbook.close();
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

        // Times from SQL
        Statement sql = speedsOfSQLConnection.createStatement();
        ResultSet speedsSQL = sql.executeQuery(sqlQuery);

        // Times from File
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
        //Sheet sheet = workbook.createSheet("Insertions " + noOfInsertions + " Blob Size " + blobSize + " " + sheetNo);
        Sheet sheet = workbook.createSheet();

        // Header row.
        Row header = sheet.createRow(0);
        Cell firstHeader = header.createCell(0);
        firstHeader.setCellValue("DB Type");
        Cell sqlHeader = header.createCell(1);
        sqlHeader.setCellValue("SQL");
        Cell fileHeader = header.createCell(2);
        fileHeader.setCellValue("File");

        // Lowest values.
        Row lowestVals = sheet.createRow(1);
        Cell lowestHeader = lowestVals.createCell(0);
        lowestHeader.setCellValue("Lowest");
        Cell sqlLowest = lowestVals.createCell(1);
        sqlLowest.setCellValue(lowestSQL);
        Cell fileLowest = lowestVals.createCell(2);
        fileLowest.setCellValue(lowestFile);

        // Lower quartile.
        Row lowerQuartileVals = sheet.createRow(2);
        Cell lowerQuarterHeader = lowerQuartileVals.createCell(0);
        lowerQuarterHeader.setCellValue("Lower Quartile");
        Cell sqlLowerQuart = lowerQuartileVals.createCell(1);
        sqlLowerQuart.setCellValue(lowerQuartileSQL);
        Cell fileLowerQuart = lowerQuartileVals.createCell(2);
        fileLowerQuart.setCellValue(lowerQuartileFile);

        // Upper quartile.
        Row upperQuartileVals = sheet.createRow(3);
        Cell upperQuartHeader = upperQuartileVals.createCell(0);
        upperQuartHeader.setCellValue("Upper Quartile");
        Cell sqlUpperQuart = upperQuartileVals.createCell(1);
        sqlUpperQuart.setCellValue(upperQuartileSQL);
        Cell fileUpperQuart = upperQuartileVals.createCell(2);
        fileUpperQuart.setCellValue(upperQuartileFile);

        // Average values.
        Row averages = sheet.createRow(4);
        Cell averagesHeader = averages.createCell(0);
        averagesHeader.setCellValue("Average");
        Cell sqlAverage = averages.createCell(1);
        sqlAverage.setCellValue(averageSQL);
        Cell fileAverage = averages.createCell(2);
        fileAverage.setCellValue(averageFile);

        // Highest values.
        Row highestVals = sheet.createRow(5);
        Cell highestHeader = highestVals.createCell(0);
        highestHeader.setCellValue("Highest");
        Cell sqlHighest = highestVals.createCell(1);
        sqlHighest.setCellValue(highestSQL);
        Cell fileHighest = highestVals.createCell(2);
        fileHighest.setCellValue(highestFile);

        // Metadata row.
        Row buffer = sheet.createRow(6);

        Row metadata = sheet.createRow(7);
        Cell blobSizeH = metadata.createCell(0);
        blobSizeH.setCellValue("Blob size");
        Cell blobSizeE = metadata.createCell(1);
        blobSizeE.setCellValue(blobSize);
        Cell insertionH = metadata.createCell(2);
        insertionH.setCellValue("Insertions");
        Cell insertionE = metadata.createCell(3);
        insertionE.setCellValue(noOfInsertions);
    }
    
    
}
