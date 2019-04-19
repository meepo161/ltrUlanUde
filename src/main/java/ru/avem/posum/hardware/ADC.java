package ru.avem.posum.hardware;

import ru.avem.posum.utils.RingBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class ADC extends Module {
    public enum Settings {
        ADC_MODE("ADC mode"), CALIBRATION_COEFFICIENTS("Calibration coefficients"),
        FACTORY_CALIBRATION_COEFFICIENTS("Factory calibration coefficients"),
        LOGIC_CHANNELS_COUNT("Logic channels count"), IIR("IIR"),
        FIR("FIR"), DECIMATION("Decimation"), TAP("Filter order"),
        REFERENCE_VOLTAGE("Reference voltage"), REFERENCE_VOLTAGE_TYPE("Reference voltage type"),
        FREQUENCY("Frequency");

        private String settingName;

        Settings(String settingName) {
            this.settingName = settingName;
        }

        public String getSettingName() {
            return settingName;
        }
    }

    private HashMap<String, Integer> bounds = new HashMap<>();
    private RingBuffer ringBufferForCalculation;
    private RingBuffer ringBufferForShow;
    private ArrayList<List<Double>> calibrationCoefficients;
    private ArrayList<List<String>> calibrationSettings;
    private final static int CHANNELS = 4; // 4 канала, поскольку все АЦП в проекте настроены на 4-х канальный режим
    private int[] channelsTypes;
    private double[] data;
    private int[] measuringRanges;
    HashMap<String, Integer> moduleSettings;
    private double[] timeMarks;
    private RingBuffer timeMarksRingBuffer;

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

    public abstract double getFrequency();

    public abstract StringBuilder moduleSettingsToString();

    public abstract void parseModuleSettings(String settings);

    public abstract void write(double[] data, double[] timeMarks);

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

    public RingBuffer getRingBufferForCalculation() {
        return ringBufferForCalculation;
    }

    public RingBuffer getRingBufferForShow() {
        return ringBufferForShow;
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

    public void setCalibrationCoefficients(ArrayList<List<Double>> calibrationCoefficients) {
        this.calibrationCoefficients = calibrationCoefficients;
    }

    public void setCalibrationSettings(ArrayList<List<String>> calibrationSettings) {
        this.calibrationSettings = calibrationSettings;
    }

    public void setData(double[] data) {
        this.data = data;
    }

    public void setRingBufferForCalculation(RingBuffer ringBufferForCalculation) {
        this.ringBufferForCalculation = ringBufferForCalculation;
    }

    public void setRingBufferForShow(RingBuffer ringBufferForShow) {
        this.ringBufferForShow = ringBufferForShow;
    }

    public void setTimeMarks(double[] timeMarks) {
        this.timeMarks = timeMarks;
    }

    public void setTimeMarksRingBuffer(RingBuffer timeMarksRingBuffer) {
        this.timeMarksRingBuffer = timeMarksRingBuffer;
    }
}
