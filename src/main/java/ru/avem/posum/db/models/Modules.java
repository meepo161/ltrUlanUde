package ru.avem.posum.db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private String dc;

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
        dc = moduleSettings.getOrDefault("Dc", "");
        frequencies = moduleSettings.getOrDefault("Frequencies", "");
        phases = moduleSettings.getOrDefault("Phases", "");
        settings = moduleSettings.getOrDefault("Module HardwareSettings", "");
    }

    private String settingsToString(int[] settings) {
        StringBuffer settingsLine = new StringBuffer();

        for (Object value : settings) {
            settingsLine.append(value).append(", ");
        }

        return String.valueOf(settingsLine);
    }

    private String settingsToString(double[] settings) {
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

    public String getChannelsDescriptions() {
        return channelsDescription;
    }

    public static List<Pair<Integer, String>> getChannelsDescriptions(Modules module) {
        List<Pair<Integer, String>> outputList = new ArrayList<>();
        String[] checkedChannels = module.getCheckedChannels().split(", ");
        String[] descriptions = module.getChannelsDescriptions().split(", ");
        String description;

        for (int channelIndex = 0; channelIndex < checkedChannels.length; channelIndex++) {
            if (checkedChannels[channelIndex].equals("true")) {
                if (channelIndex < descriptions.length) { // если описание существует
                    description = String.format("%s (модуль %s, слот %d)", descriptions[channelIndex], module.getModuleType(), module.getSlot());
                } else {
                    description = String.format("Канал %d (модуль %s, слот %d)", (channelIndex + 1), module.getModuleType(), module.getSlot());
                }
                outputList.add(new Pair<>(channelIndex + 1, description));
            }
        }

        return outputList;
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

    public static double getAmplitude(Modules module, int channel) {
        String[] amplitudes = module.getAmplitudes().split(", ");
        return Double.parseDouble(amplitudes[channel - 1]);
    }

    public String getDc() {
        return dc;
    }

    public static double getDc(Modules module, int channel) {
        String[] dc = module.getDc().split(", ");
        return Double.parseDouble(dc[channel - 1]);
    }

    public String getFrequencies() {
        return frequencies;
    }

    public static int getFrequency(Modules module, int channel) {
        String[] frequencies = module.getFrequencies().split(", ");
        return Integer.parseInt(frequencies[channel - 1]);
    }

    public static String getModuleName(Modules module) {
        return String.format("модуль %s, слот %d", module.getModuleType(), module.getSlot());
    }

    public String getPhases() {
        return phases;
    }

    public static int getPhase(Modules module, int channel) {
        String[] phases = module.getPhases().split(", ");
        return Integer.parseInt(phases[channel - 1]);
    }

    public String getSettings() {
        return settings;
    }

    public void setAmplitudes(double[] amplitudes) {
        this.amplitudes = settingsToString(amplitudes);
    }

    public void setDc(double[] dc) {
        this.dc = settingsToString(dc);
    }

    public void setFrequencies(int[] frequencies) {
        this.frequencies = settingsToString(frequencies);
    }

    public void setPhases(int[] phases) {
        this.phases = settingsToString(phases);
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }
}
