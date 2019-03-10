package ru.avem.posum.hardware;

import ru.avem.posum.models.CalibrationModel;

import java.util.ArrayList;
import java.util.List;

public class ADC extends Module {
    int[] channelsTypes;
    int[] measuringRanges;
    private String[] channelsDescription;
    private List<CalibrationModel> calibrationModel;

    ADC() {
        channelsCount = 4; // 4 канала, поскольку все АЦП в проекте настроены на 4-х канальный режим
        checkedChannels = new boolean[channelsCount];
        channelsTypes = new int[channelsCount];
        measuringRanges = new int[channelsCount];
        channelsDescription = new String[channelsCount];
        calibrationModel = new ArrayList<>();
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
}
