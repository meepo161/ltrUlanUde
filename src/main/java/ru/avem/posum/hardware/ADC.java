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
    private ArrayList<List<Double>> calibrationCoefficients;
    private ArrayList<List<String>> calibrationSettings;
    private final static int CHANNELS = 4; // 4 канала, поскольку все АЦП в проекте настроены на 4-х канальный режим
    private String[] channelsDescription;
    int[] channelsTypes;
    private double[] data;
    int[] measuringRanges;
    int arraysPerSecond;
    HashMap<String, Integer> moduleSettings;
    private RingBuffer receivedData;
    private double[] receivedDataBuffer;
    private RingBuffer receivedTimeMarks;
    private double[] receivedTimeMarksBuffer;
    double[] timeMarks;
    double bufferedTimeMark;

    ADC() {
        channelsCount = CHANNELS;
        checkedChannels = new boolean[channelsCount];
        channelsTypes = new int[channelsCount];
        measuringRanges = new int[channelsCount];
        timeMarks = new double[8192];
        channelsDescription = new String[channelsCount];
        calibrationCoefficients = new ArrayList<>();
        calibrationSettings = new ArrayList<>();
        moduleSettings = new HashMap<>();
    }

    public abstract StringBuilder moduleSettingsToString();

    public abstract void parseModuleSettings(String settings);

    public double[] getReceivedDataBuffer() {
        return receivedDataBuffer;
    }

    public void setReceivedDataBuffer(double[] receivedDataBuffer) {
        this.receivedDataBuffer = receivedDataBuffer;
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

    public String[] getChannelsDescription() {
        return channelsDescription;
    }

    public int[] getChannelsTypes() {
        return channelsTypes;
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

    public HashMap<String, Integer> getModuleSettings() {
        return moduleSettings;
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

    public double[] getTimeMarks() {
        return timeMarks;
    }

    public void setReceivedTimeMarks(RingBuffer receivedTimeMarks) {
        this.receivedTimeMarks = receivedTimeMarks;
    }

    public double[] getReceivedTimeMarksBuffer() {
        return receivedTimeMarksBuffer;
    }

    public void setReceivedTimeMarksBuffer(double[] receivedTimeMarksBuffer) {
        this.receivedTimeMarksBuffer = receivedTimeMarksBuffer;
    }

    public void setTimeMarks(double[] timeMarks) {
        this.timeMarks = timeMarks;
    }
}
