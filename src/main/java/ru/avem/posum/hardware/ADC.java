package ru.avem.posum.hardware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class ADC extends Module {
    public enum Settings {
        ADC_MODE("ADC mode"), CALIBRATION_COEFFICIENTS("Calibration coefficients"),
        FACTORY_CALIBRATION_COEFFICIENTS("Factory calibration coefficients"),
        LOGIC_CHANNELS_COUNT("Logic channels count"), IIR_FILTER("IIR filter"),
        FIR_FILTER("FIR filter"), DECIMATION("Decimation"), TAP("Filter order"),
        REFERENCE_VOLTAGE("Reference voltage"), REFERENCE_VOLTAGE_TYPE("Reference voltage type");

        private String settingName;

        Settings(String settingName) {
            this.settingName = settingName;
        }

        public String getSettingName() {
            return settingName;
        }
    }

    private HashMap<String, Integer> bounds = new HashMap<>();
    private ArrayList<List<Double>> calibrationCoefficients;
    private ArrayList<List<String>> calibrationSettings;
    private final static int CHANNELS = 4; // 4 канала, поскольку все АЦП в проекте настроены на 4-х канальный режим
    private String[] channelsDescription;
    private int[] channelsTypes;
    private double[] data;
    private double[] dataBuffer;
    private int[] measuringRanges;
    HashMap<String, Integer> moduleSettings;
    private double[] timeMarks;
    private double[] timeMarksBuffer;

    ADC() {
        channelsCount = CHANNELS;
        checkedChannels = new boolean[channelsCount];
        channelsTypes = new int[channelsCount];
        measuringRanges = new int[channelsCount];
        channelsDescription = new String[channelsCount];
        calibrationCoefficients = new ArrayList<>();
        calibrationSettings = new ArrayList<>();
        moduleSettings = new HashMap<>();
    }

    public abstract StringBuilder moduleSettingsToString();

    public abstract void parseModuleSettings(String settings);

    public HashMap<String, Integer> getBounds() {
        return bounds;
    }

    public ArrayList<List<Double>> getCalibrationCoefficients() {
        return calibrationCoefficients;
    }

    public ArrayList<List<String>> getCalibrationSettings() {
        return calibrationSettings;
    }

    public String[] getChannelsDescription() {
        return channelsDescription;
    }

    public int[] getChannelsTypes() {
        return channelsTypes;
    }

    public double[] getData() {
        return data;
    }

    public double[] getDataBuffer() {
        return dataBuffer;
    }

    public int[] getMeasuringRanges() {
        return measuringRanges;
    }

    public HashMap<String, Integer> getModuleSettings() {
        return moduleSettings;
    }

    public double[] getTimeMarks() {
        return timeMarks;
    }

    public double[] getTimeMarksBuffer() {
        return timeMarksBuffer;
    }

    public void setBounds(HashMap<String, Integer> bounds) {
        this.bounds = bounds;
    }

    public void setDataBuffer(double[] dataBuffer) {
        this.dataBuffer = dataBuffer;
    }

    public void setCalibrationCoefficients(ArrayList<List<Double>> calibrationCoefficients) {
        this.calibrationCoefficients = calibrationCoefficients;
    }

    public void setCalibrationSettings(ArrayList<List<String>> calibrationSettings) {
        this.calibrationSettings = calibrationSettings;
    }

    public void setData(double[] data) {
        this.data = data;
    }

    public void setTimeMarks(double[] timeMarks) {
        this.timeMarks = timeMarks;
    }

    public void setTimeMarksBuffer(double[] timeMarksBuffer) {
        this.timeMarksBuffer = timeMarksBuffer;
    }
}
