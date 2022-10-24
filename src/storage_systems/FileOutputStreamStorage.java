package src.storage_systems;

/**
 * Testing how fast it is to read data that was written directly with
 * FileOutputStream.
 * Technically would be faster as the files will only be storing the raw byte
 * data of the waveform itself - however, it could be more useful to have a
 * SQLite database, where you can quickly get a specific time of recording.
 * 
 * 
 * There would be a lot of blobs to read through, these blobs are the waveform data itself.
 */
public class FileOutputStreamStorage implements IStorageSystem {

    @Override
    public void prepareStorage() {
        // TODO Auto-generated method stub

        /**
         * Seeing how fast it is to read data from different files to get different
         * times.
         */

    }

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
        // TODO Auto-generated method stub

    }

}
