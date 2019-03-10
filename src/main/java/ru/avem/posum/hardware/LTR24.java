package ru.avem.posum.hardware;

public class LTR24 extends ADC {
    public void openConnection() {
        status = open(crate, slot);
        checkStatus();
    }

    public native String open(String crate, int slot);

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

    public void closeConnection() {
        close(slot);
        checkStatus();
    }

    public native String close(int slot);

    static {
        System.loadLibrary( "LTR24Library");
    }
}
