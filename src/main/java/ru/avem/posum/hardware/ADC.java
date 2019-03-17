package ru.avem.posum.hardware;

import java.util.ArrayList;
import java.util.List;

public class ADC extends Module {
    int[] channelsTypes;
    int[] measuringRanges;
    private String[] channelsDescription;
    private ArrayList<List<String>> calibrationSettings;

    ADC() {
        channelsCount = 4; // 4 канала, поскольку все АЦП в проекте настроены на 4-х канальный режим
        checkedChannels = new boolean[channelsCount];
        channelsTypes = new int[channelsCount];
        measuringRanges = new int[channelsCount];
        channelsDescription = new String[channelsCount];
        calibrationSettings = new ArrayList<>();
    }

    public int[] getChannelsTypes() {
        return channelsTypes;
    }

    public int[] getMeasuringRanges() {
        return measuringRanges;
    }

    public String[] getChannelsDescription() {
        return channelsDescription;
    }

    public ArrayList<List<String>> getCalibrationSettings() {
        return calibrationSettings;
    }

    public void setCalibrationSettings(ArrayList<List<String>> calibrationSettings) {
        this.calibrationSettings = calibrationSettings;
    }
}
