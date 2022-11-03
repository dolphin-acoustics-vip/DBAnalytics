package src;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

public class CreateRandomData {

    private String shipName, channel;
    private LocalDateTime dateRecorded;
    private long durationOfWaveformSound;
    private byte[] waveformData;
    private String[] shipNames;

    public CreateRandomData(int blobSize) {

        Random rand = new Random();

        // Setting random ship name and channel recorded on.
        shipNames = new String[] { "Ship 1", "Ship 2", "Ship 3", "Ship 4", "Ship 5" };
        int randomShip = rand.nextInt(shipNames.length);
        setShipName(shipNames[randomShip]);
        setChannel(String.valueOf(rand.nextInt(12) + 1));

        // Setting random duration.
        long duration = 5;
        setDurationOfWaveformSound(duration);

        // Creating a random date and time recorded.
        int minYear = 1980;
        int maxYear = 2022;
        int randomYear = (int) Math.floor(Math.random() * (maxYear - minYear + 1) + minYear);
        int randomMonth = rand.nextInt(12) + 1;
        int randomDay;
        if (randomMonth == 2 && randomYear % 4 == 0) {
            randomDay = rand.nextInt(29) + 1;
        } else if (randomMonth == 2 && randomYear % 4 != 0) {
            randomDay = rand.nextInt(28) + 1;
        } else if (randomMonth == 9 || randomMonth == 4 || randomMonth == 6 || randomMonth == 11) {
            randomDay = rand.nextInt(30) + 1;
        } else {
            randomDay = rand.nextInt(31) + 1;
        }
        // Making the date
        LocalDate randomDate = LocalDate.of(randomYear, randomMonth, randomDay);

        int randomHour = rand.nextInt(24);
        int randomMin = rand.nextInt(60);
        int randomSec = rand.nextInt(60);
        // Setting the time itself
        LocalDateTime randomStartTime = randomDate.atTime(randomHour, randomMin, randomSec);
        setDateRecorded(randomStartTime);

        /**
         * How to set a random value for the byte array
         * https://stackoverflow.com/questions/5683206/how-to-create-an-array-of-20-random-bytes
         */
        // Setting a random blob.
        byte[] wvData = new byte[blobSize];
        try {
            SecureRandom.getInstanceStrong().nextBytes(wvData);
            setWaveformData(wvData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    public LocalDateTime getDateRecorded() {
        return dateRecorded;
    }

    public void setDateRecorded(LocalDateTime dateRecorded) {
        this.dateRecorded = dateRecorded;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public long getDurationOfWaveformSound() {
        return durationOfWaveformSound;
    }

    public void setDurationOfWaveformSound(long durationOfWaveformSound) {
        this.durationOfWaveformSound = durationOfWaveformSound;
    }

    public byte[] getWaveformData() {
        return waveformData;
    }

    public void setWaveformData(byte[] waveformData) {
        this.waveformData = waveformData;
    }
}
