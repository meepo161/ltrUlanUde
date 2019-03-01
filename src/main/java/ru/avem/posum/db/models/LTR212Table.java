package ru.avem.posum.db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "ltr212Modules")
public class LTR212Table {
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField
    private long testProgramId;
    @DatabaseField
    private String crate;
    @DatabaseField
    private String slot;
    @DatabaseField
    private String checkedChannels;
    @DatabaseField
    private String channelsTypes;
    @DatabaseField
    private String measuringRanges;
    @DatabaseField
    private String channelsDescription;
    @DatabaseField
    private String calibrationOfChannelN1;
    @DatabaseField
    private String calibrationOfChannelN2;
    @DatabaseField
    private String calibrationOfChannelN3;
    @DatabaseField
    private String calibrationOfChannelN4;

    public LTR212Table() {
        // ORMLite and XML binder need a no-arg constructor
    }

    public LTR212Table(long testProgramId, String[][] moduleSettings) {
        this.testProgramId = testProgramId;
        this.crate = moduleSettings[0][0];
        this.slot = moduleSettings[1][0];
        this.checkedChannels = settingsToString(moduleSettings[2]);
        this.channelsTypes = settingsToString(moduleSettings[3]);
        this.measuringRanges = settingsToString(moduleSettings[4]);
        this.channelsDescription = settingsToString(moduleSettings[5]);
        this.calibrationOfChannelN1 = moduleSettings[6][0];
        this.calibrationOfChannelN2 = moduleSettings[6][1];
        this.calibrationOfChannelN3 = moduleSettings[6][2];
        this.calibrationOfChannelN4 = moduleSettings[6][3];
    }

    public static String settingsToString(String[] settings) {
        StringBuilder sb = new StringBuilder();

        for (String value: settings) {
            sb.append(value).append(", ");
        }

        return sb.toString();
    }

    public long getTestProgramId() {
        return testProgramId;
    }

    public void setTestProgramId(long testProgramId) {
        this.testProgramId = testProgramId;
    }

    public String getCrate() {
        return crate;
    }

    public void setCrate(String crate) {
        this.crate = crate;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getCheckedChannels() {
        return checkedChannels;
    }

    public void setCheckedChannels(String checkedChannels) {
        this.checkedChannels = checkedChannels;
    }

    public String getChannelsTypes() {
        return channelsTypes;
    }

    public void setChannelsTypes(String channelsTypes) {
        this.channelsTypes = channelsTypes;
    }

    public String getMeasuringRanges() {
        return measuringRanges;
    }

    public void setMeasuringRanges(String measuringRanges) {
        this.measuringRanges = measuringRanges;
    }

    public String getChannelsDescription() {
        return channelsDescription;
    }

    public void setChannelsDescription(String channelsDescription) {
        this.channelsDescription = channelsDescription;
    }

    public String getCalibrationOfChannelN1() {
        return calibrationOfChannelN1;
    }

    public void setCalibrationOfChannelN1(String calibrationOfChannelN1) {
        this.calibrationOfChannelN1 = calibrationOfChannelN1;
    }

    public String getCalibrationOfChannelN2() {
        return calibrationOfChannelN2;
    }

    public void setCalibrationOfChannelN2(String calibrationOfChannelN2) {
        this.calibrationOfChannelN2 = calibrationOfChannelN2;
    }

    public String getCalibrationOfChannelN3() {
        return calibrationOfChannelN3;
    }

    public void setCalibrationOfChannelN3(String calibrationOfChannelN3) {
        this.calibrationOfChannelN3 = calibrationOfChannelN3;
    }

    public String getCalibrationOfChannelN4() {
        return calibrationOfChannelN4;
    }

    public void setCalibrationOfChannelN4(String calibrationOfChannelN4) {
        this.calibrationOfChannelN4 = calibrationOfChannelN4;
    }
}
