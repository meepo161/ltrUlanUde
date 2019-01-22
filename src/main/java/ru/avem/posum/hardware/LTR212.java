package ru.avem.posum.hardware;

public class LTR212 {
    private int[] checkedChannels = new int[8];
    private int[] channelsTypes = new int[8];
    private int[] measuringRanges = new int[8];
    private int slot;

    public LTR212() {
        setDefaultParameters();
    }

    private void setDefaultParameters() {
        for (int i = 0; i < channelsTypes.length; i++) {
            channelsTypes[i] = 1;
            measuringRanges[i] = 3;
        }
    }

    public native String initialize(int slot, int[] selectedBridgeTypes, int[] selectedMeasuringRanges);

    public native String fillArray(double[] data);

    public native String stop();

    public int[] getCheckedChannels() {
        return checkedChannels;
    }

    public int[] getChannelsTypes() {
        return channelsTypes;
    }

    public int[] getMeasuringRanges() {
        return measuringRanges;
    }

    static {
//        System.loadLibrary("LTR212Library");
    }
}
