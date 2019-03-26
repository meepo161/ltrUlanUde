package ru.avem.posum.hardware;

public class LTR24 extends ADC {
    public void openConnection() {
        status = open(crate, getSlot());
        checkStatus();
    }

    public native String open(String crate, int slot);

    public void initModule() {
        status = initialize(getSlot(), getChannelsTypes(), getMeasuringRanges());
        checkStatus();
    }

    public native String initialize(int slot, int[] channelsTypes, int[] measuringRanges);

    public void receive(double[] data) {
        status = fillArray(getSlot(), data);
        checkStatus();
    }

    public native String fillArray(int slot, double[] data);

    public void closeConnection() {
        close(getSlot());
        checkStatus();
    }

    public native String close(int slot);

    static {
        System.loadLibrary( "LTR24Library");
    }
}
