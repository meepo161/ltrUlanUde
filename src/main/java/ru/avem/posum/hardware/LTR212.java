package ru.avem.posum.hardware;

public class LTR212 extends ADC {
    public void openConnection() {
        status = open(crate, slot, System.getProperty("user.dir").replace("\\", "/") + "/ltr212.bio");
        checkStatus();
    }

    public native String open(String crate, int slot, String path);

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
        System.loadLibrary( "LTR212Library");
    }
}
