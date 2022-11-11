package src;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import src.storage_systems.PopulateSystem;

public class Main {
    public static void main(String[] args) {

        // gatherSpeeds();

    }

    public static void gatherSpeeds() {
        File script = new File("scripts/databaseAnalysis.txt");
        new CreateSQLiteDatabase("speedsOfFileOutputDatabase", script);
        new CreateSQLiteDatabase("speedsOfSQLDatabase", script);

        File sizes = new File("databaseSizes.txt");
        if (sizes.delete()) {
        } else {
            try {
                sizes.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Made speeds databases.");

        int[] numberOfInsertions = new int[] { 100, 1000, 10000, 100000 };
        int[] blobSizes = new int[] { 50, 100, 500, 1000, 10000 };

        for (int i = 0; i < numberOfInsertions.length; i++) {
            for (int j = 0; j < blobSizes.length; j++) {
                try {
                    new PopulateSystem(numberOfInsertions[i], blobSizes[j], sizes);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 8 * 50 = 400 bytes = 0.4kb
         * - 40 kb
         * - 400 kb
         * - 4000 kb = 4mb
         * - 40000 kb = 40mb
         * 8 * 100 = 800 bytes = 0.8kb
         * - 80kb
         * - 800 kb
         * - 8000 kb = 8mb
         * - 80000 kb = 80mb
         * 8 * 500 = 4000 bytes = 4kb
         * - 400 kb
         * - 4000 kb = 4mb
         * - 40000 kb = 40mb
         * - 400000 kb = 400mb
         * 8 * 1000 = 8000 bytes = 8kb
         * - 800 kb
         * - 8000 kb = 8mb
         * - 80000 kb = 80mb
         * - 800000 kb = 800mb (100,000 insertions)
         * 
         * Total of over 1333mb = 1.33 GB (So I do need to clean it right after each -
         * also explains why it's so long with a higher number of insertions)
         */
    }
}
