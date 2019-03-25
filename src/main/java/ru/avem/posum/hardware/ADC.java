package ru.avem.posum.hardware;

import ru.avem.posum.utils.RingBuffer;

import java.util.ArrayList;
import java.util.List;

public class ADC extends Module {
    private double[] buffer;
    private ArrayList<List<Double>> calibrationCoefficients = new ArrayList<>();
    private ArrayList<List<String>> calibrationSettings;
    private String[] channelsDescription;
    int[] channelsTypes;
    private double[] data;
    int[] measuringRanges;
    int[] moduleSettings = {0, 0, 0, 4, 0, 0, 0, 0, 1, 0};
    double[] timeMarks = new double[4096];
    private RingBuffer ringBuffer;

    ADC() {
        channelsCount = 4; // 4 канала, поскольку все АЦП в проекте настроены на 4-х канальный режим
        checkedChannels = new boolean[channelsCount];
        channelsTypes = new int[channelsCount];
        measuringRanges = new int[channelsCount];
        channelsDescription = new String[channelsCount];
        calibrationSettings = new ArrayList<>();
    }

    public double[] getBuffer() {
        return buffer;
    }

    public void setBuffer(double[] buffer) {
        this.buffer = buffer;
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

    public int[] getModuleSettings() {
        return moduleSettings;
    }

    public RingBuffer getRingBuffer() {
        return ringBuffer;
    }

    public void setRingBuffer(RingBuffer ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public double[] getTimeMarks() {
        return timeMarks;
    }
}
