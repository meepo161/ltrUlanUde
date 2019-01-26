package ru.avem.posum.hardware;

public class LTR34 {
    private boolean[] checkedChannels = new boolean[8];
    private int channelsCounter;
    private int slot;

    public void countChannels() {
        for (boolean channel : checkedChannels) {
            if (channel) {
                channelsCounter++;
            }
        }
    }

    public native String initialize(String crateSN, int slot, int channelsCounter, boolean[] checkedChannels);

    public native void dataSend(double[] data);

    public native String start();

    public native String stop();

    static {
//        System.loadLibrary("LTR34Library");
    }
}
