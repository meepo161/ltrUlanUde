package ru.avem.posum.hardware;

public class LTR34 {
    private int[] checkedChannels = new int[8];
    private int[] channelsFrequency = new int[8];
    private int[] measuringAmplitude = new int[8];
    private int slot;

    public native String initialize(int slot);

    public native void dataSend(double[] data);

    public native String start();

    public native String stop();

    public int[] getCheckedChannels() {
        return checkedChannels;
    }

    public int[] getChannelsFrequency() {
        return channelsFrequency;
    }

    public int[] getMeasuringAmplitude() {
        return measuringAmplitude;
    }

    static {
//        System.loadLibrary("LTR34Library");
    }
}
