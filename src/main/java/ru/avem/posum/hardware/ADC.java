package ru.avem.posum.hardware;

import ru.avem.posum.utils.RingBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Класс АЦП модуля
 */

public abstract class ADC extends Module {
    // Виды настроек АЦП модулей
    public enum Settings {
        ADC_MODE("ADC mode"), CALIBRATION_COEFFICIENTS("calibration coefficients"),
        FACTORY_CALIBRATION_COEFFICIENTS("Factory calibration coefficients"),
        LOGIC_CHANNELS_COUNT("Logic channels count"), IIR("IIR"),
        FIR("FIR"), DECIMATION("Decimation"), TAP("Filter order"),
        REFERENCE_VOLTAGE("Reference voltage"), REFERENCE_VOLTAGE_TYPE("Reference voltage type"),
        FREQUENCY("Frequency");

        private String settingName;

        Settings(String settingName) {
            this.settingName = settingName;
        }
    }

    // Пределы измерений канала модуля АЦП
    public enum MeasuringRangeOfChannel {
        LOWER_BOUND(0), UPPER_BOUND(0);

        private double boundValue;

        MeasuringRangeOfChannel(double boundValue) {
            this.boundValue = boundValue;
        }

        public void setBoundValue(double boundValue) {
            this.boundValue = boundValue;
        }

        public double getBoundValue() {
            return boundValue;
        }
    }

    private HashMap<String, Integer> bounds = new HashMap<>(); // пределы измерений
    private ArrayList<List<Double>> calibrationCoefficients; // калибровочные коэффициенты
    private ArrayList<List<String>> calibrationSettings; // настройки калибровки
    private double[] data; // данные модуля
    private String firPath; // путь к КИХ фильтру
    private String iirPath; // путь к БИХ фильру
    private int[] measuringRanges; // диапазоны измерений каналов
    private RingBuffer ringBufferForCalculation; // кольцевой буфер для расчета параметров принятого сигнала
    private RingBuffer ringBufferForShow; // кольцевой буфер для оторажения принятого сигнала
    HashMap<Settings, Integer> settingsOfModule; // настройки модуля
    private double[] timeMarks; // синхрометки
    private RingBuffer timeMarksRingBuffer; // кольцевой буфер для синхрометок
    private int[] typeOfChannels; // режим работы каналов

    ADC() {
        channelsCount = 4; // 4 канала, поскольку все АЦП в проекте настроены на 4-х канальный режим
        calibrationCoefficients = new ArrayList<>();
        calibrationSettings = new ArrayList<>();
        checkedChannels = new boolean[channelsCount];
        firPath = "";
        iirPath = "";
        measuringRanges = new int[channelsCount];
        descriptions = new String[channelsCount];
        settingsOfModule = new HashMap<>();
        typeOfChannels = new int[channelsCount];
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

    public double[] getData() {
        return data;
    }

    public String[] getDescriptions() {
        return descriptions;
    }

    public String getFirPath() {
        return firPath;
    }

    public String getIirPath() {
        return iirPath;
    }

    public int[] getMeasuringRanges() {
        return measuringRanges;
    }

    public RingBuffer getRingBufferForCalculation() {
        return ringBufferForCalculation;
    }

    public RingBuffer getRingBufferForShow() {
        return ringBufferForShow;
    }

    public HashMap<Settings, Integer> getSettingsOfModule() {
        return settingsOfModule;
    }

    public double[] getTimeMarks() {
        return timeMarks;
    }

    public int[] getTypeOfChannels() {
        return typeOfChannels;
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

    public void setFirPath(String firPath) {
        this.firPath = firPath;
    }

    public void setIirPath(String iirPath) {
        this.iirPath = iirPath;
    }

    public void setMeasuringRanges(int[] measuringRanges) {
        this.measuringRanges = measuringRanges;
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

    public void setTypeOfChannels(int[] typeOfChannels) {
        this.typeOfChannels = typeOfChannels;
    }
}
