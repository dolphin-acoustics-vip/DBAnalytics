package src;

public class CreateRandomData {

    private String dateRecorded, shipName, channel, startRecordTime, endRecordTime;
    public String getDateRecorded() {
        return dateRecorded;
    }

    public void setDateRecorded(String dateRecorded) {
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

    public String getStartRecordTime() {
        return startRecordTime;
    }

    public void setStartRecordTime(String startRecordTime) {
        this.startRecordTime = startRecordTime;
    }

    public String getEndRecordTime() {
        return endRecordTime;
    }

    public void setEndRecordTime(String endRecordTime) {
        this.endRecordTime = endRecordTime;
    }

    public long getDurationOfWaveformSound() {
        return durationOfWaveformSound;
    }

    public void setDurationOfWaveformSound(long durationOfWaveformSound) {
        this.durationOfWaveformSound = durationOfWaveformSound;
    }

    public byte getWaveformData() {
        return waveformData;
    }

    public void setWaveformData(byte waveformData) {
        this.waveformData = waveformData;
    }

    private long durationOfWaveformSound;
    private byte waveformData;
    
    CreateRandomData() {

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
