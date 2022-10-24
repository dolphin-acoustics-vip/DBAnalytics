package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class will create a SQLite database (for various purposes throughout the
 * program)
 */
public class CreateSQLiteDatabase {

    private String databaseURL, databaseName;
    private File script;

    public CreateSQLiteDatabase(String dbName, File s) {

        databaseName = dbName;
        databaseURL = "jdbc:sqlite:" + databaseName + ".db";
        script = s;

        makeDBFile();
        checkConnection();
        createTables();
    }

    private void makeDBFile() {
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

    }

    /**
     * 
     */
    private void checkConnection() {
        Connection conn = makeConnection();
        if (conn != null) {
            System.out.println("Able to connect to database.");
        } else {
            System.out.println("Cannot connect to database.");
        }
        try {
            conn.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void createTables() {
        Connection conn = makeConnection();
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(script));

            StringBuilder makeTable = new StringBuilder();

            String line = br.readLine();
            while (line != null) {
                makeTable.append(line);
                makeTable.append(System.lineSeparator());
                line = br.readLine();
            }

            String[] statements = makeTable.toString().split("#");

            java.sql.Statement stm = conn.createStatement();
            for (int i = 0; i < statements.length; i++) {
                stm.execute(statements[i]);
            }

            conn.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection makeConnection() {
        try {
            return DriverManager.getConnection(databaseURL);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
