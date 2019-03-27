package ru.avem.posum.hardware;

import ru.avem.posum.utils.RingBuffer;

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

    int arraysCounter;
    int arraysPerSecond;
    double bufferedTimeMark;
    private ArrayList<List<Double>> calibrationCoefficients;
    private ArrayList<List<String>> calibrationSettings;
    private final static int CHANNELS = 4; // 4 канала, поскольку все АЦП в проекте настроены на 4-х канальный режим
    private String[] channelsDescription;
    private int[] channelsTypes;
    private double[] data;
    private double[] dataBuffer;
    private int[] measuringRanges;
    HashMap<String, Integer> moduleSettings;
    private RingBuffer receivedData;
    private RingBuffer receivedTimeMarks;
    double[] timeMarks;

    ADC() {
        channelsCount = CHANNELS;
        checkedChannels = new boolean[channelsCount];
        channelsTypes = new int[channelsCount];
        measuringRanges = new int[channelsCount];
        timeMarks = new double[4096];
        channelsDescription = new String[channelsCount];
        calibrationCoefficients = new ArrayList<>();
        calibrationSettings = new ArrayList<>();
        moduleSettings = new HashMap<>();
    }

    public abstract StringBuilder moduleSettingsToString();

    public abstract void parseModuleSettings(String settings);

    public int getArraysCounter() {
        return arraysCounter;
    }

    public void setArraysCounter(int arraysCounter) {
        this.arraysCounter = arraysCounter;
    }

    public int getArraysPerSecond() {
        return arraysPerSecond;
    }

    public void setArraysPerSecond(int arraysPerSecond) {
        this.arraysPerSecond = arraysPerSecond;
    }

    public double getBufferedTimeMark() {
        return bufferedTimeMark;
    }

    public void setBufferedTimeMark(double bufferedTimeMark) {
        this.bufferedTimeMark = bufferedTimeMark;
    }

    public ArrayList<List<Double>> getCalibrationCoefficients() {
        return calibrationCoefficients;
    }

    public void setCalibrationCoefficients(ArrayList<List<Double>> calibrationCoefficients) {
        this.calibrationCoefficients = calibrationCoefficients;
    }

    public ArrayList<List<String>> getCalibrationSettings() {
        return calibrationSettings;
    }

    public void setCalibrationSettings(ArrayList<List<String>> calibrationSettings) {
        this.calibrationSettings = calibrationSettings;
    }

    public static int getCHANNELS() {
        return CHANNELS;
    }

    public String[] getChannelsDescription() {
        return channelsDescription;
    }

    public void setChannelsDescription(String[] channelsDescription) {
        this.channelsDescription = channelsDescription;
    }

    public int[] getChannelsTypes() {
        return channelsTypes;
    }

    public void setChannelsTypes(int[] channelsTypes) {
        this.channelsTypes = channelsTypes;
    }

    public double[] getData() {
        return data;
    }

    public void setData(double[] data) {
        this.data = data;
    }

    public int[] getMeasuringRanges() {
        return measuringRanges;
    }

    public void setMeasuringRanges(int[] measuringRanges) {
        this.measuringRanges = measuringRanges;
    }

    public HashMap<String, Integer> getModuleSettings() {
        return moduleSettings;
    }

    public void setModuleSettings(HashMap<String, Integer> moduleSettings) {
        this.moduleSettings = moduleSettings;
    }

    public RingBuffer getReceivedData() {
        return receivedData;
    }

    public void setReceivedData(RingBuffer receivedData) {
        this.receivedData = receivedData;
    }

    public RingBuffer getReceivedTimeMarks() {
        return receivedTimeMarks;
    }

    public void setReceivedTimeMarks(RingBuffer receivedTimeMarks) {
        this.receivedTimeMarks = receivedTimeMarks;
    }

    public double[] getDataBuffer() {
        return dataBuffer;
    }

    public void setDataBuffer(double[] dataBuffer) {
        this.dataBuffer = dataBuffer;
    }

    public double[] getTimeMarks() {
        return timeMarks;
    }

    public void setTimeMarks(double[] timeMarks) {
        this.timeMarks = timeMarks;
    }
}
