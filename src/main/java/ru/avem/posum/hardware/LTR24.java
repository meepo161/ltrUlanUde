package ru.avem.posum.hardware;

public class LTR24 {
    public native String initialize(int slot, int[] selectedBridgeTypes, int[] selectedMeasuringRanges);

    public native String fillArray(double[] data);

    public native String stop();

    static {
        System.loadLibrary("LTR24Library");
    }
}
