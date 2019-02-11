package ru.avem.posum.db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Objects;

@DatabaseTable(tableName = "ltr34Modules")
public class LTR34Module {
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField
    private long testProgrammId;
    @DatabaseField
    private String checkedChannels = "";
    @DatabaseField
    private String channelsFrequency = "";
    @DatabaseField
    private String channelsAmplitude = "";
    @DatabaseField
    private String crate = "";
    @DatabaseField
    private int slot;

    public LTR34Module() {
        // ORMLite and XML binder need a no-arg constructor
    }

    public LTR34Module(long testProgrammId, boolean[] checkedChannels, int[][] channelsParameters, String crate, int slot) {
        this.testProgrammId = testProgrammId;

        for (boolean checked : checkedChannels) {
            if (checked) {
                this.checkedChannels += "1, ";
            } else {
                this.checkedChannels += "0, ";
            }
        }

        for (int frequency : channelsParameters[0]) {
            this.channelsFrequency += frequency + ", ";
        }

        for (int amplitude : channelsParameters[1]) {
            this.channelsAmplitude += amplitude + ", ";
        }

        this.crate = crate;
        this.slot = slot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LTR34Module ltr34Module = (LTR34Module) o;
        return  id == ltr34Module.id &&
                Objects.equals(checkedChannels, ltr34Module.checkedChannels) &&
                Objects.equals(channelsFrequency, ltr34Module.channelsFrequency) &&
                Objects.equals(channelsAmplitude, ltr34Module.channelsAmplitude) &&
                Objects.equals(crate, ltr34Module.crate) &&
                Objects.equals(slot, ltr34Module.slot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, checkedChannels, channelsFrequency, channelsAmplitude, crate, slot);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTestProgrammId() {
        return testProgrammId;
    }

    public void setTestProgrammId(long testProgrammId) {
        this.testProgrammId = testProgrammId;
    }

    public String getCheckedChannels() {
        return checkedChannels;
    }

    public void setCheckedChannels(String checkedChannels) {
        this.checkedChannels = checkedChannels;
    }

    public String getChannelsFrequency() {
        return channelsFrequency;
    }

    public void setChannelsFrequency(String channelsFrequency) {
        this.channelsFrequency = channelsFrequency;
    }

    public String getChannelsAmplitude() {
        return channelsAmplitude;
    }

    public void setChannelsAmplitude(String channelsAmplitude) {
        this.channelsAmplitude = channelsAmplitude;
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
