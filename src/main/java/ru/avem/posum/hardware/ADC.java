package ru.avem.posum.hardware;

import ru.avem.posum.models.CalibrationModel;

import java.util.ArrayList;
import java.util.List;

public abstract class ADC extends Module {
    private int[] channelsTypes;
    private int[] measuringRanges;
    private String[] channelsDescription;
    private List<CalibrationModel> calibrationModel;

    public ADC() {
        channelsCount = 4; // 4 канала, поскольку все АЦП в проекте настроены на 4-х канальный режим
        checkedChannels = new boolean[channelsCount];
        channelsTypes = new int[channelsCount];
        measuringRanges = new int[channelsCount];
        channelsDescription = new String[channelsCount];
        calibrationModel = new ArrayList<>();
    }

    @Override
    public void initModule() {
        status = initialize(slot, channelsTypes, measuringRanges);
        checkStatus();
    }

    public native String initialize(int slot, int[] channelsTypes, int[] measuringRanges);

    public void receive(double[] data) {
        status = fillArray(slot, data);
        checkStatus();
    }

    public native String fillArray(int slot, double[] data);

    @Override
    public void closeConnection() {
        close(slot);
        checkStatus();
    }

    public native String close(int slot);

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
