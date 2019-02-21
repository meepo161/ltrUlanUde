package ru.avem.posum.hardware;

public interface ADC {
    String[] getCalibrationSettings();

    boolean[] getCheckedChannels();

    int[] getChannelsTypes();

    int[] getMeasuringRanges();

    String[] getChannelsDescription();

    String getCrate();

    int getSlot();
}
