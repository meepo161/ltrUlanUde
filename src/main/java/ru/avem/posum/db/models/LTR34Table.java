package ru.avem.posum.db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "ltr34Modules")
public class LTR34Table {
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField
    private long testProgramId;
    @DatabaseField
    private String checkedChannels = "";
    @DatabaseField
    private String channelsAmplitude = "";
    @DatabaseField
    private String channelsFrequency = "";
    @DatabaseField
    private String channelsPhase = "";
    @DatabaseField
    private String crate = "";
    @DatabaseField
    private int slot;

    public LTR34Table() {
        // ORMLite and XML binder need a no-arg constructor
    }

    public LTR34Table(long testProgramId, boolean[] checkedChannels, int[][] channelsParameters, String crate, int slot) {
        this.testProgramId = testProgramId;

        for (boolean checked : checkedChannels) {
            if (checked) {
                this.checkedChannels += "1, ";
            } else {
                this.checkedChannels += "0, ";
            }
        }

        for (int amplitude : channelsParameters[0]) {
            this.channelsAmplitude += amplitude + ", ";
        }

        for (int frequency : channelsParameters[1]) {
            this.channelsFrequency += frequency + ", ";
        }

        for (int phase : channelsParameters[2]) {
            this.channelsPhase += phase + ", ";
        }

        this.crate = crate;
        this.slot = slot;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTestProgramId() {
        return testProgramId;
    }

    public void setTestProgramId(long testProgramId) {
        this.testProgramId = testProgramId;
    }

    public String getCheckedChannels() {
        return checkedChannels;
    }

    public void setCheckedChannels(String checkedChannels) {
        this.checkedChannels = checkedChannels;
    }

    public String getChannelsAmplitude() {
        return channelsAmplitude;
    }

    public void setChannelsAmplitudes(String channelsAmplitude) {
        this.channelsAmplitude = channelsAmplitude;
    }

    public String getChannelsFrequency() {
        return channelsFrequency;
    }

    public void setChannelsFrequencies(String channelsFrequency) {
        this.channelsFrequency = channelsFrequency;
    }

    public String getChannelsPhase() {
        return channelsPhase;
    }

    public void setChannelsPhases(String channelsPhase) {
        this.channelsPhase = channelsPhase;
    }

    public String getCrate() {
        return crate;
    }

    public void setCrate(String crate) {
        this.crate = crate;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
}
