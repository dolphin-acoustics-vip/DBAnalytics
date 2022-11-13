public class AnalyseData {

    // This class will get the data from the SQLite databases and plot it on a graph.
    AnalyseData(int[] numberOfInsertions, int[] blobSizes) {

    }

    private void getTotalTime(int noOfInsertions, int blobSize) {

        // Get all the times for a certain blob size and number of insertions.
        String sqlQuery = "SELECT duration FROM table WHERE blobSize IS " + blobSize + " AND noOfInsertions IS " + noOfInsertions;

        // After getting all the times, get the average.
        // Also get the longest and smallest time.
        // Get the difference between these times.
        // Present this visually in some form of graph (automatically or manually).

    }
    
    
}
