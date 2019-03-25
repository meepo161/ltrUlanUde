package ru.avem.posum.hardware;

import java.util.ArrayList;
import java.util.List;

public class ADC extends Module {
    private ArrayList<List<Double>> calibrationCoefficients = new ArrayList<>();
    private ArrayList<List<String>> calibrationSettings;
    int[] channelsTypes;
    int[] measuringRanges;
    int[] moduleSettings = {0, 0, 0, 4, 0, 0, 0, 0, 1, 0};
    double[] timeMarks = new double[4096];
    private String[] channelsDescription;

    ADC() {
        channelsCount = 4; // 4 канала, поскольку все АЦП в проекте настроены на 4-х канальный режим
        checkedChannels = new boolean[channelsCount];
        channelsTypes = new int[channelsCount];
        measuringRanges = new int[channelsCount];
        channelsDescription = new String[channelsCount];
        calibrationSettings = new ArrayList<>();
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

    public int[] getChannelsTypes() {
        return channelsTypes;
    }

    public int[] getMeasuringRanges() {
        return measuringRanges;
    }

    public int[] getModuleSettings() {
        return moduleSettings;
    }

    public String[] getChannelsDescription() {
        return channelsDescription;
    }

    public double[] getTimeMarks() {
        return timeMarks;
    }
}
