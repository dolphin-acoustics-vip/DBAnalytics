package src;

import java.io.File;

import src.storage_systems.FileOutputStreamStorage;
import src.storage_systems.SQLiteStorageSystem;

public class Main {
    public static void main(String[] args) {

        // Blob size, i'm assuming, will be the biggest factor in time differences between these two data storage systems, the metadato in the SQLDatabase would be the same for any blob size.

        // 100 insertions, with the blobs being 50

        // 1000 insertions

        File script = new File("scripts/databaseAnalysis.txt");
        new CreateSQLiteDatabase("speedsOfFileOutputDatabase", script);
        new CreateSQLiteDatabase("speedsOfSQLDatabase", script);

        System.out.println("Made speeds databases.");

        
        int [] numberOfInsertions = new int[] {100, 1000, 10000, 100000, 1000000};
        int [] blobSizes = new int[] {50, 100, 500, 1000, 10000};

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
