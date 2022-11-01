package src;

import java.io.File;

import src.storage_systems.FileOutputStreamStorage;
import src.storage_systems.SQLiteStorageSystem;

public class Main {
    public static void main(String[] args) {

        // Blob size, i'm assuming, will be the biggest factor in time differences between these two data storage systems, the metadato in the SQLDatabase would be the same for any blob size.
        File script = new File("scripts/databaseAnalysis.txt");
        new CreateSQLiteDatabase("speedsOfFileOutputDatabase", script);
        new CreateSQLiteDatabase("speedsOfSQLDatabase", script);

        System.out.println("Made speeds databases.");
        System.out.println("=====");

        
        int [] numberOfInsertions = new int[] {100, 1000, 10000, 100000};
        int [] blobSizes = new int[] {50, 100, 500, 1000};
        /**
         * 8 * 50 = 400 bytes = 0.4kb
         *      - 40 kb
         *      - 400 kb
         *      - 4000 kb = 4mb
         *      - 40000 kb = 40mb
         * 8 * 100 = 800 bytes = 0.8kb
         *      - 80kb
         *      - 800 kb
         *      - 8000 kb = 8mb 
         *      - 80000 kb = 80mb
         * 8 * 500 = 4000 bytes = 4kb
         *      - 400 kb
         *      - 4000 kb = 4mb
         *      - 40000 kb = 40mb
         *      - 400000 kb = 400mb
         * 8 * 1000 = 8000 bytes = 8kb
         *      - 800 kb
         *      - 8000 kb = 8mb
         *      - 80000 kb = 80mb
         *      - 800000 kb = 800mb (100,000 insertions)
         * 
         * Total of over 1333mb = 1.33 GB (So I do need to clean it right after each - also explains why it's so long with a higher number of insertions)
         */

        for (int insertion = 0; insertion < numberOfInsertions.length; insertion++) {
            for (int blobSize = 0; blobSize < blobSizes.length; blobSize++) {
                System.out.println("Number of Insertions = " + numberOfInsertions[insertion]);
                System.out.println("Blob Size being inserted = " + blobSizes[blobSize]); 
                new SQLiteStorageSystem("SQLiteStorageSystem", numberOfInsertions[insertion], blobSizes[blobSize]);
                new FileOutputStreamStorage(numberOfInsertions[insertion], blobSizes[blobSize]);
                System.out.println("Done");
                System.out.println("");
            }
        }
        

    }
}
