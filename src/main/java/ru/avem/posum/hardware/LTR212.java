package ru.avem.posum.hardware;

public class LTR212 {
    public native String initialize(int slot, int[] selectedBridgeTypes, int[] selectedMeasuringRanges);

    public native String fillArray(double[] data);

    public native String stop();

    static {
        System.loadLibrary("LTR212Library");
    }
}
