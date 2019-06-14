package ru.avem.posum.db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import javafx.util.Pair;
import ru.avem.posum.db.CalibrationsRepository;

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
    private String typesOfChannels;

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

    @DatabaseField
    private String channelsCount;

    @DatabaseField
    private String firPath;

    @DatabaseField
    private String iirPath;

    @DatabaseField
    private String dataLength;

    public Modules() {
        // ORMLite and XML binder need a no-arg constructor
    }

    public Modules(HashMap<String, String> moduleSettings) {
        testProgramId = Long.parseLong(moduleSettings.get("Test program id"));
        moduleType = moduleSettings.get("Module type");
        slot = moduleSettings.get("Slot");
        checkedChannels = moduleSettings.getOrDefault("Checked channels", "");
        typesOfChannels = moduleSettings.getOrDefault("Channels types", "");
        measuringRanges = moduleSettings.getOrDefault("Measuring ranges", "");
        channelsDescription = moduleSettings.getOrDefault("Channels description", "");
        amplitudes = moduleSettings.getOrDefault("Amplitudes", "");
        dc = moduleSettings.getOrDefault("Dc", "");
        frequencies = moduleSettings.getOrDefault("Frequencies", "");
        phases = moduleSettings.getOrDefault("Phases", "");
        settings = moduleSettings.getOrDefault("Module HardwareSettings", "");
        channelsCount = moduleSettings.getOrDefault("Channels count", "");
        firPath = moduleSettings.getOrDefault("FIR path", "");
        iirPath = moduleSettings.getOrDefault("IIR path", "");
        dataLength = moduleSettings.getOrDefault("Data length", "0");
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

    public long getTestProgramId() {
        return testProgramId;
    }

    public String getModuleType() {
        return moduleType;
    }

    public int getSlot() {
        return Integer.parseInt(slot);
    }

    public String getCheckedChannels() {
        return checkedChannels;
    }

    public static List<Integer> getCheckedChannels(Modules module) {
        List<Integer> checkedChannels = new ArrayList<>();
        String[] splitChannels = module.getCheckedChannels().split(", ");

        for (int channelIndex = 0; channelIndex < splitChannels.length; channelIndex++) {
            if (splitChannels[channelIndex].equals("true")) {
                checkedChannels.add(channelIndex);
            }
        }

        return checkedChannels;
    }

    public String getTypesOfChannels() {
        return typesOfChannels;
    }

    public static int[] getTypesOfChannels(Modules module) {
        String[] splitTypes = module.getTypesOfChannels().split(", ");
        int typesCount = splitTypes.length;
        int[] typesOfChannels = new int[typesCount];

        for (int typeIndex = 0; typeIndex < typesCount; typeIndex++) {
            typesOfChannels[typeIndex] = Integer.parseInt(splitTypes[typeIndex]);
        }

        return typesOfChannels;
    }

    public String getMeasuringRanges() {
        return measuringRanges;
    }

    public static int[] getMeasuringRanges(Modules module) {
        String[] splitRanges = module.getMeasuringRanges().split(", ");
        int rangesCount = splitRanges.length;
        int[] measuringRanges = new int[rangesCount];

        for (int typeIndex = 0; typeIndex < rangesCount; typeIndex++) {
            measuringRanges[typeIndex] = Integer.parseInt(splitRanges[typeIndex]);
        }

        return measuringRanges;
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

    public String getAmplitudes() {
        return amplitudes;
    }

    public static double[] getAmplitudes(Modules module) {
        String[] splitAmplitudes = module.getAmplitudes().split(", ");
        double[] amplitudes = new double[splitAmplitudes.length];

        for (int i = 0; i < amplitudes.length; i++) {
            amplitudes[i] = Double.parseDouble(splitAmplitudes[i]);
        }

        return amplitudes;
    }

    public static double getAmplitude(Modules module, int channel) {
        String[] amplitudes = module.getAmplitudes().split(", ");
        return Double.parseDouble(amplitudes[channel - 1]);
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTestProgramId(long testProgramId) {
        this.testProgramId = testProgramId;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public void setCheckedChannels(boolean[] checkedChannels) {
        StringBuffer settingsLine = new StringBuffer();

        for (boolean value : checkedChannels) {
            settingsLine.append(value).append(", ");
        }

        this.checkedChannels = String.valueOf(settingsLine);
    }

    public void setTypesOfChannels(int[] typesOfChannels) {
        this.typesOfChannels = settingsToString(typesOfChannels);
    }

    public void setMeasuringRanges(int[] measuringRanges) {
        this.measuringRanges = settingsToString(measuringRanges);
    }

    public static List<String> getCalibrations(Modules module, int channel) {
        List<Calibration> calibrations = CalibrationsRepository.getAllCalibrations();
        for (Calibration calibration : calibrations) {
            if (calibration.getModuleId() == module.getId()) {
                return calibration.getCalibrationSettings().get(channel);
            }
        }
        return new ArrayList<>();
    }

    public void setChannelsDescription(String[] channelsDescription) {
        StringBuffer settingsLine = new StringBuffer();

        for (String value : channelsDescription) {
            settingsLine.append(value).append(", ");
        }

        this.channelsDescription = String.valueOf(settingsLine);
    }

    public String getDc() {
        return dc;
    }

    public static double[] getDc(Modules module) {
        String[] splitDc = module.getDc().split(", ");
        double[] dc = new double[splitDc.length];

        for (int i = 0; i < dc.length; i++) {
            dc[i] = Double.parseDouble(splitDc[i]);
        }

        return dc;
    }

    public static double getDc(Modules module, int channel) {
        String[] dc = module.getDc().split(", ");
        return Double.parseDouble(dc[channel - 1]);
    }

    public String getFrequencies() {
        return frequencies;
    }

    public static double[] getFrequencies(Modules module) {
        String[] splitFrequencies = module.getFrequencies().split(", ");
        double[] frequencies = new double[splitFrequencies.length];

        for (int i = 0; i < frequencies.length; i++) {
            frequencies[i] = Double.parseDouble(splitFrequencies[i]);
        }

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

    public String getSettings() {
        return settings;
    }

    public static int[] getSettingsOfModule(Modules module) {
        String[] splitSettings = module.getSettings().split(", ");
        int settingsCount = splitSettings.length;
        int[] settingsOfModule = new int[settingsCount];

        for (int settingsIndex = 0; settingsIndex < settingsCount; settingsIndex++) {
            settingsOfModule[settingsIndex] = Integer.parseInt(splitSettings[settingsIndex]);
        }

        return settingsOfModule;
    }

    public int getChannelsCount() {
        return Integer.parseInt(channelsCount);
    }

    public String getFirPath() {
        return firPath;
    }

    public String getIirPath() {
        return iirPath;
    }

    public int getDataLength() {
        return Integer.parseInt(dataLength);
    }

    public void setAmplitudes(double[] amplitudes) {
        this.amplitudes = settingsToString(amplitudes);
    }

    public void setDc(double[] dc) {
        this.dc = settingsToString(dc);
    }

    public void setFrequencies(double[] frequencies) {
        this.frequencies = settingsToString(frequencies);
    }

    public void setPhases(int[] phases) {
        this.phases = settingsToString(phases);
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    public void setChannelsCount(int channelsCount) {
        this.channelsCount = String.valueOf(channelsCount);
    }

    public void setFirPath(String firPath) {
        this.firPath = firPath;
    }

    public void setIirPath(String iirPath) {
        this.iirPath = iirPath;
    }

    public void setDataLength(String dataLength) {
        this.dataLength = dataLength;
    }
}
