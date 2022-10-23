package src;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;

public class CreateRandomData {

    private String shipName, channel;
    private LocalDateTime startRecordTime, endRecordTime;
    private LocalDate dateRecorded;
    private long durationOfWaveformSound;
    private byte[] waveformData;
    private Timestamp startTimestamp, endTimestamp;

    public Timestamp getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Timestamp startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Timestamp getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(Timestamp endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    private String[] shipNames;

    public LocalDate getDateRecorded() {
        return dateRecorded;
    }

    public void setDateRecorded(LocalDate dateRecorded) {
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

    public LocalDateTime getStartRecordTime() {
        return startRecordTime;
    }

    public void setStartRecordTime(LocalDateTime startRecordTime) {
        this.startRecordTime = startRecordTime;
    }

    public LocalDateTime getEndRecordTime() {
        return endRecordTime;
    }

    public void setEndRecordTime(LocalDateTime endRecordTime) {
        this.endRecordTime = endRecordTime;
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
    
    CreateRandomData(int blobSize) {

        shipNames = new String[] {"Ship 1", "Ship 2", "Ship 3", "Ship 4", "Ship 5"};

        /**
         * https://www.baeldung.com/java-random-dates
         * tried 3.1 - didn't work
         */
        int randomYear = (int) Math.random() * (2022 - 1980);
        
        int randomMonthNo = (int) Math.random() * (12 - 1);

        LocalDate randomDate = LocalDate.of(2022, 2, 15);
        setDateRecorded(randomDate);

        setChannel(String.valueOf((int) Math.random() * (12 - 1)));

        int randomShip = (int) Math.random() * (shipNames.length - 1);
        setShipName(shipNames[randomShip]);

        // Add this time onto the startRecordTime
        // Is between 1nanosec and 5secs
        //long duration = (long) Math.random() * (100 - 1);
        long duration = 5;
        setDurationOfWaveformSound(duration);

        /**
         * https://stackoverflow.com/questions/14771845/generating-random-date-time-in-java-joda-time
         */

         /**
          * Creating random start and end recording time.
          */
        int randomHour = (int) Math.random() * (24 - 1);
        int randomMin = (int) Math.random() * (60 - 1);
        int randomSec = (int) Math.random() * (60 - 1);
        int randomNanoSec = (int) Math.random() * (1000000 - 1);
        LocalDateTime randomStartTime = dateRecorded.atTime(randomHour, randomMin, randomSec, randomNanoSec);

        setStartRecordTime(randomStartTime);
        setEndRecordTime(getStartRecordTime().plusNanos(duration));

        Timestamp startTS = new Timestamp(randomStartTime.toEpochSecond(ZoneOffset.UTC));
        setStartTimestamp(startTS);
        Timestamp endTS = new Timestamp(randomStartTime.plusNanos(duration).toEpochSecond(ZoneOffset.UTC));
        setEndTimestamp(endTS);



        /** How to set a random value for the byte array
         * https://stackoverflow.com/questions/5683206/how-to-create-an-array-of-20-random-bytes
         */
        byte[] wvData = new byte[blobSize];
        try {
            SecureRandom.getInstanceStrong().nextBytes(wvData);
            setWaveformData(wvData);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 
     * @return The path of the data file.
     */
    public String makeFile() {

        /**
         * - CHANNEL RECORDED ON
         *  - SHIP RECORDED ON (NULL OTHERWISSE)
         *  - DATE RECORDED
         *  - TIME RECORDED
         *  - LENGTH RECORDED
         */


        return "";
    }

}
