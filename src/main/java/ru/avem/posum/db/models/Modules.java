package ru.avem.posum.db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.HashMap;

@DatabaseTable(tableName = "modules")
public class Modules {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private long testProgramId;

    @DatabaseField
    private String moduleType;

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
    private String amplitudes;

    @DatabaseField
    private String frequencies;

    @DatabaseField
    private String phases;

    @DatabaseField
    private String settings;

    public Modules() {
        // ORMLite and XML binder need a no-arg constructor
    }

    public Modules(HashMap<String, String> moduleSettings) {
        testProgramId = Long.parseLong(moduleSettings.get("Test program id"));
        moduleType = moduleSettings.get("Module type");
        slot = moduleSettings.get("Slot");
        checkedChannels = moduleSettings.getOrDefault("Checked channels", "");
        channelsTypes = moduleSettings.getOrDefault("Channels types", "");
        measuringRanges = moduleSettings.getOrDefault("Measuring ranges", "");
        channelsDescription = moduleSettings.getOrDefault("Channels description", "");
        amplitudes = moduleSettings.getOrDefault("Amplitudes", "");
        frequencies = moduleSettings.getOrDefault("Frequencies", "");
        phases = moduleSettings.getOrDefault("Phases", "");
        settings = moduleSettings.getOrDefault("Module Settings", "");
    }

    private String settingsToString(int[] settings) {
        StringBuffer settingsLine = new StringBuffer();

        for (Object value : settings) {
            settingsLine.append(value).append(", ");
        }

        return String.valueOf(settingsLine);
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

    public String getModuleType() {
        return moduleType;
    }

    public int getSlot() {
        return Integer.parseInt(slot);
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getCheckedChannels() {
        return checkedChannels;
    }

    public void setCheckedChannels(boolean[] checkedChannels) {
        StringBuffer settingsLine = new StringBuffer();

        for (boolean value : checkedChannels) {
            settingsLine.append(value).append(", ");
        }

        this.checkedChannels = String.valueOf(settingsLine);
    }

    public String getChannelsTypes() {
        return channelsTypes;
    }

    public void setChannelsTypes(int[] channelsTypes) {
        this.channelsTypes = settingsToString(channelsTypes);
    }

    public String getMeasuringRanges() {
        return measuringRanges;
    }

    public void setMeasuringRanges(int[] measuringRanges) {
        this.measuringRanges = settingsToString(measuringRanges);
    }

    public String getChannelsDescription() {
        return channelsDescription;
    }

    public void setChannelsDescription(String[] channelsDescription) {
        StringBuffer settingsLine = new StringBuffer();

        for (String value : channelsDescription) {
            settingsLine.append(value).append(", ");
        }

        this.channelsDescription = String.valueOf(settingsLine);
    }

    public String getAmplitudes() {
        return amplitudes;
    }

    public void setAmplitudes(int[] amplitudes) {
        this.amplitudes = settingsToString(amplitudes);
    }

    public String getFrequencies() {
        return frequencies;
    }

    public void setFrequencies(int[] frequencies) {
        this.frequencies = settingsToString(frequencies);
    }

    public String getPhases() {
        return phases;
    }

    public void setPhases(int[] phases) {
        this.phases = settingsToString(phases);
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }
}
